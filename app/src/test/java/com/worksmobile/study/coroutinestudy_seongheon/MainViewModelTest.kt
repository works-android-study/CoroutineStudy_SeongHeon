package com.worksmobile.study.coroutinestudy_seongheon

import androidx.paging.PagingData
import androidx.work.Data
import androidx.work.WorkInfo
import app.cash.turbine.test
import com.worksmobile.study.coroutinestudy_seongheon.common.TestCoroutineDispatcher
import com.worksmobile.study.coroutinestudy_seongheon.data.ImageRepository
import com.worksmobile.study.coroutinestudy_seongheon.data.Item
import com.worksmobile.study.coroutinestudy_seongheon.data.SearchResultType
import com.worksmobile.study.coroutinestudy_seongheon.download.DownloadCenter
import com.worksmobile.study.coroutinestudy_seongheon.download.DownloadState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@ExperimentalCoroutinesApi
@ExtendWith(TestCoroutineDispatcher::class)
class MainViewModelTest {
    @Rule
    @JvmField
    val dispatcher = TestCoroutineDispatcher()

    @Mock
    lateinit var mockRepository: ImageRepository

    @Mock
    lateinit var mockDownloadCenter: DownloadCenter

    private val mockItemList = listOf(
        Item("title1", "link", "thumbnail", 0, 0),
        Item("title2", "link", "thumbnail", 0, 0)
    )

    private val mockPagingData = PagingData.from(mockItemList)

    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mainViewModel = MainViewModel(mockRepository, mockDownloadCenter)

        `when`(mockRepository.searchImages(any())).thenReturn(flow { emit(mockPagingData) })
        `when`(mockRepository.selectBookmarkImage()).thenReturn(flow { emit(mockPagingData) })
    }

    @Test
    @DisplayName("쿼리 데이터가 변경되었을때의 테스트")
    fun test_change_query() = runTest {
        mainViewModel.changeQuery("test")
        assertEquals("test", mainViewModel.searchQueryFlow.value)
    }

    @Test
    @DisplayName("확정된 쿼리가 비어있을 때 테스트")
    fun test_handle_query_if_empty_query() = runTest {
        mainViewModel.pagingDataFlow.test {
            mainViewModel.changeQuery("")
            mainViewModel.handleQuery()
            awaitItem()

            assertEquals(SearchResultType.LOCAL, mainViewModel.searchResultTypeFlow.value)
            verify(mockRepository).selectBookmarkImage()
        }
    }

    @Test
    @DisplayName("확정된 쿼리가 비어있지 않을 때 테스트")
    fun test_handle_query_if_not_empty_query() = runTest {
        mainViewModel.pagingDataFlow.test {
            mainViewModel.changeQuery("test")
            mainViewModel.handleQuery()
            awaitItem()

            assertEquals(SearchResultType.REMOTE, mainViewModel.searchResultTypeFlow.value)
            verify(mockRepository).searchImages("test")
        }
    }

    @Test
    @DisplayName("이미지 다운로드 테스트 성공")
    fun test_download_image_download_center() = runTest {
        val item = Item("title1", "link", "thumbnail", 0, 0)
        val mockWorkInfo = mock(WorkInfo::class.java)
        `when`(mockWorkInfo.state).thenReturn(WorkInfo.State.SUCCEEDED)
        `when`(mockWorkInfo.outputData).thenReturn(Data.EMPTY)
        `when`(mockWorkInfo.outputData.getString(DownloadCenter.KEY_FOR_LINK)).thenReturn("test uri")
        `when`(mockDownloadCenter.startDownload(item, any())).thenAnswer {
            it.getArgument<(WorkInfo?) -> Unit>(0).invoke(mockWorkInfo)
        }

        mainViewModel.downloadEventFlow.test {
            mainViewModel.downloadImage(item)
            val result = awaitItem()

            assertEquals(DownloadState.Complete("test uri"), result)
        }
    }
}
