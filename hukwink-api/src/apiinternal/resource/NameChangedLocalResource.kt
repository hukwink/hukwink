package com.hukwink.hukwink.apiinternal.resource

import com.hukwink.hukwink.resource.LocalResource

internal class NameChangedLocalResource(
    delegate: LocalResource,
    override val fileName: String,
) : LocalResource by delegate
