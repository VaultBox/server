package dev.medzik.librepass.server.controllers.api.v1

import dev.medzik.librepass.server.components.AuthorizedUser
import dev.medzik.librepass.server.database.UserTable
import dev.medzik.librepass.server.services.UserService
import dev.medzik.librepass.server.utils.Response
import dev.medzik.librepass.server.utils.ResponseError
import dev.medzik.librepass.server.utils.ResponseSuccess
import dev.medzik.librepass.types.api.user.ChangePasswordRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController {
    @Autowired
    private lateinit var userService: UserService

    @PatchMapping("/password")
    fun changePassword(
        @AuthorizedUser user: UserTable?,
        @RequestBody body: ChangePasswordRequest
    ): Response {
        if (user == null) return ResponseError.Unauthorized

        return if (userService.changePassword(user, body)) {
            ResponseSuccess.OK
        } else {
            ResponseError.InvalidBody
        }
    }
}