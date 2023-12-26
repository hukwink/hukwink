package com.hukwink.hukwink.util

@RequiresOptIn(
    message = "This `public` api is only for hukwink internal usage.",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
public annotation class HukwinkInternalApi

@RequiresOptIn(
    message = "This api is under experimental",
    level = RequiresOptIn.Level.WARNING,
)
@Retention(AnnotationRetention.BINARY)
public annotation class HukwinkExperimentalApi

