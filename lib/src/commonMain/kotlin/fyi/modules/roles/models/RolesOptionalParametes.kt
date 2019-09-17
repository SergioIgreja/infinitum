package fyi.modules.roles.models

import fyi.utils.Args
import fyi.utils.OptionalParameters

/**
 * Class that contains information about all the [OptionalParameters] the Roles module requests can have.
 * It also knows how this information should be constructed in the body of the request.
 *
 * @property mBuilder Builder responsible for setting the optional parameters.
 */
data class RolesOptionalParametes private constructor(
    val mBuilder: Builder
): OptionalParameters {

    /**
     * Transform this [OptionalParameters] to a map.
     */
    override fun toMap(): MutableMap<String, String> {
        val result = Args.createMap(
            Pair("name", mBuilder.mName),
            Pair("alias", mBuilder.mAlias)
        )
        result.putAll(Args.createMap("permissions", mBuilder.mPermissions))

        return result
    }

    /**
     * Builder class to facilitate the insertion of optional data.
     *
     * @property mName Role name.
     * @property mAlias Role alias.
     * @property mPermissions Role permissions.
     */
    class Builder() {
        var mName = ""
        var mAlias= ""
        var mPermissions = mutableListOf<String>()

        fun addName(name: String): Builder {
            mName = name
            return this
        }

        fun addAlias(alias: String): Builder {
            mAlias = alias
            return this
        }

        fun addPermission(permission: String): Builder {
            mPermissions.add(permission)
            return this
        }

        fun addPermissions(permissions: MutableList<String>) {
            mPermissions.addAll(permissions)
        }

        internal fun build(): RolesOptionalParametes {
            return RolesOptionalParametes(this)
        }
    }
}