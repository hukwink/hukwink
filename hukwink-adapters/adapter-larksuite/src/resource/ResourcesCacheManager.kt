package com.hukwink.hukwink.adapter.larksuite.resource

import com.hukwink.hukwink.adapter.larksuite.LarksuiteBot
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteHttpResponseProcess.ensureOk
import com.hukwink.hukwink.adapter.larksuite.http.LarksuiteHttpResponseProcess.parseToJsonAndVerify
import com.hukwink.hukwink.adapter.larksuite.http.larksuiteAuthorization
import com.hukwink.hukwink.resource.LocalResource
import com.hukwink.hukwink.resource.withAutoUse
import com.hukwink.hukwink.util.childScope
import io.vertx.core.buffer.Buffer
import io.vertx.ext.web.multipart.MultipartForm
import io.vertx.kotlin.coroutines.coAwait
import kotlinx.coroutines.job
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.h2.jdbcx.JdbcConnectionPool

internal class ResourcesCacheManager(
    private val bot: LarksuiteBot,
) {
    private val scope = bot.coroutineScope.coroutineContext.childScope("")

    val databaseSource by lazy {
        JdbcConnectionPool.create(
            "jdbc:h2:file:" + bot.configuration.dataFolder.resolve("resource-cache.h2.db").absolutePath,
            null,
            null
        ).also { pool ->
            scope.coroutineContext.job.invokeOnCompletion { pool.dispose() }
        }
    }

    fun initialize() {
        databaseSource.connection.use { conn ->
            conn.createStatement().use { stat ->
                stat.executeUpdate(
                    """
create table IF NOT EXISTS uploaded_resources
(
    file_type            varchar(8),
    file_sha1            binary(20),
    file_md5             binary(16),
    file_size            long,
    file_name            varchar(256),
    uploaded_resource_id varchar(256),
    constraint uploaded_resources_pk
        primary key (file_type, file_sha1, file_md5, file_size, file_name)
);
                """
                )
            }
        }
    }

    suspend fun upload(resource: LocalResource, subType: SubType): String = resource.withAutoUse {
        databaseSource.connection.use { conn ->
            val providedName = when (subType) {
                SubType.IMAGE -> ""
                SubType.FILE -> resource.fileName
            }
            conn.prepareStatement(
                """SELECT uploaded_resource_id from uploaded_resources
                |where file_md5 = ?
                |and file_sha1 = ?
                |and file_size = ?
                |and file_type = ?
                |and file_name = ?
                |""".trimMargin()
            ).use { ps ->
                ps.setBytes(1, resource.md5)
                ps.setBytes(2, resource.sha1)
                ps.setLong(3, resource.size)
                ps.setString(4, subType.dbType)
                ps.setString(5, providedName)
                ps.executeQuery().use { result ->
                    if (result.next()) {
                        return@withAutoUse result.getString(1)
                    }
                }
            }

            if (resource.size > 10 * 1024L * 1024L) {
                error("Resource To Large")
            }

            val multiPartForm = MultipartForm.create().setCharset(Charsets.UTF_8)
            val uploadReply = when (subType) {
                SubType.IMAGE -> {
                    multiPartForm.binaryFileUpload(
                        "image", providedName, resource.openStream().use { Buffer.buffer(it.readAllBytes()) },
                        "application/octet-stream"
                    ).attribute("image_type", "message")
                    bot.webClient
                        .post("/open-apis/im/v1/images")
                }
                SubType.FILE -> {
                    multiPartForm.binaryFileUpload(
                        "file", providedName, resource.openStream().use { Buffer.buffer(it.readAllBytes()) },
                        "application/octet-stream"
                    )
                        .attribute("file_type", "stream")
                        .attribute("file_name", resource.fileName)

                    bot.webClient
                        .post("/open-apis/im/v1/files")
                }
            }
                .larksuiteAuthorization(bot)
                .sendMultipartForm(multiPartForm)
                .coAwait()
                .ensureOk()
                .bodyAsBuffer().parseToJsonAndVerify()

            val resourceKey = when (subType) {
                SubType.FILE -> uploadReply["data"]?.jsonObject?.get("file_key")?.jsonPrimitive?.contentOrNull
                SubType.IMAGE -> uploadReply["data"]?.jsonObject?.get("image_key")?.jsonPrimitive?.contentOrNull
            } ?: error("Failed to get resource key from $uploadReply")

            kotlin.runCatching {
                conn.prepareStatement(
                    "INSERT INTO uploaded_resources (" +
                        "file_sha1, file_md5, file_size, file_type, uploaded_resource_id, file_name" +
                        ") values ( ?, ?, ?, ?, ?, ? )"
                ).use { ps ->
                    ps.setBytes(1, resource.sha1)
                    ps.setBytes(2, resource.md5)
                    ps.setLong(3, resource.size)
                    ps.setString(4, subType.dbType)
                    ps.setString(5, resourceKey)
                    ps.setString(6, providedName)
                    ps.executeUpdate()
                }
            }

            resourceKey
        }
    }

    enum class SubType(
        val dbType: String,
    ) {
        IMAGE("image"),
        FILE("file"),
        ;

    }
}