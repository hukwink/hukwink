package com.hukwink.hukwink.resource

import java.nio.file.Path

public sealed class LocalResourceOrigin {
    public data object Unknown : LocalResourceOrigin()

    public data class FromFile(public val path: Path) : LocalResourceOrigin()
}
