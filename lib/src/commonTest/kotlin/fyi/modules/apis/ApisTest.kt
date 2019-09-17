package fyi.modules.apis

import fyi.exceptions.ErrorResponse
import fyi.exceptions.Errors
import fyi.modules.apis.Apis
import fyi.modules.apis.models.ApiOptionalParameters
import fyi.modules.apis.models.ApiResponse
import fyi.repository.NetworkService
import fyi.repository.Repository
import fyi.repository.RequestLauncher
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class Callbacks {
    fun success(response: List<ApiResponse>) {}
    fun failure(error: ErrorResponse) {}
    fun successNoReturn() {}
    fun successApi(response: ApiResponse) {}
}


open class ApisTest {
    @RelaxedMockK
    lateinit var callback: Callbacks

    @BeforeTest
    fun setup() {
        callback = mockk(relaxed = true)
    }

    @Test
    fun testGetAllApisFailure() {
        val networkServiceMock = mockk<NetworkService>()
        val repositoryMock = mockk<Repository>()

        every { repositoryMock.getAccessToken() } returns ""

        val apis = Apis("teste", networkServiceMock, repositoryMock)

        apis.getAllApis(callback::success, callback::failure)

        verify { callback.failure(Errors.INVALID_TOKEN.error) }
    }

    @Test
    fun testGetAllApisSuccess() {
        //Only validates if there is a token stored

        val networkServiceMock = mockk<NetworkService>()
        val repositoryMock = mockk<Repository>()
        mockkObject(RequestLauncher)

        every { repositoryMock.getAccessToken() } returns "validtoken"
        //Stops the programs from launching Coroutines
        every { RequestLauncher.launch(any(), any(), any(), any(), any(), any(), any()) } returns Unit

        val apis = Apis("teste", networkServiceMock, repositoryMock)

        apis.getAllApis(callback::success, callback::failure)

        //Verifies if there's an attempt to launch the coroutine
        verify { RequestLauncher.launch(any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun testNewApiFails() {
        val networkServiceMock = mockk<NetworkService>()
        val repositoryMock = mockk<Repository>()

        every { repositoryMock.getAccessToken() } returns "valid token"

        val apis = Apis("teste", networkServiceMock, repositoryMock)

        apis.newApi("", 2, callback::successNoReturn, callback::failure)

        verify { callback.failure(Errors.INVALID_PARAMETER.error) }
    }

    @Test
    fun testGetApiIdInvalid() {
        val networkServiceMock = mockk<NetworkService>()
        val repositoryMock = mockk<Repository>()
        mockkObject(RequestLauncher)

        //"valid" token
        every { repositoryMock.getAccessToken() } returns "valid token"
        //Stops Coroutine from launching
        every { RequestLauncher.launch(any(), any(), any(), any(), any(), any(), any())} returns Unit

        val apis = Apis("teste", networkServiceMock, repositoryMock)

        //Invalid id, should fail
        apis.getApiById(-1, callback::successApi, callback::failure)

        verify { callback.failure(Errors.INVALID_PARAMETER.error) }
    }

    @Test
    fun testEditApiSucceed() {
        val networkServiceMock = mockk<NetworkService>()
        val repositoryMock = mockk<Repository>()
        mockkObject(RequestLauncher)

        val optionalParameters = ApiOptionalParameters.Builder()

        //"valid" token
        every { repositoryMock.getAccessToken() } returns "valid token"
        //Stops Coroutine from launching
        every { RequestLauncher.launch(any(), any(), any(), any(), any(), any(), any())} returns Unit

        val apis = Apis("teste", networkServiceMock, repositoryMock)

        //Invalid id, should fail
        apis.updateApi(-1, optionalParameters, callback::successNoReturn, callback::failure)

        verify { callback.failure(Errors.INVALID_PARAMETER.error) }
    }

}