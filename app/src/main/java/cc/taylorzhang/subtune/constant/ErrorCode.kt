package cc.taylorzhang.subtune.constant

object ErrorCode {
    /**
     * A generic error.
     */
    const val GENERIC = 0

    /**
     * Required parameter is missing.
     */
    const val MISSING_PARAMETER = 10

    /**
     * Incompatible Subsonic REST protocol version. Client must upgrade.
     */
    const val UPGRADE_CLIENT = 20

    /**
     * Incompatible Subsonic REST protocol version. Server must upgrade.
     */
    const val UPGRADE_SERVER = 30

    /**
     * Wrong username or password.
     */
    const val AUTH_FAIL = 40

    /**
     * Token authentication not supported for LDAP users.
     */
    const val NOT_SUPPORTED_LDAP = 41

    /**
     * User is not authorized for the given operation.
     */
    const val NO_PERMISSION = 50

    /**
     * The trial period for the Subsonic server is over. Please upgrade to Subsonic Premium.
     */
    const val TRIAL_EXPIRED = 60

    /**
     * The requested data was not found.
     */
    const val DATA_NOT_FOUND = 70
}