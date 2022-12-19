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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
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
    @DisplayName("이미지 다운로드 시, DownloadCenter 메소드 호출")
    fun test_download_image_download_center() = runTest {
        val item = Item("title1", "link", "thumbnail", 0, 0)

        mainViewModel.downloadImage(item)
        verify(mockDownloadCenter).startDownload(eq(item), any())
    }

    @Test
    @DisplayName("이미지 다운로드 성공")
    fun test_download_image_success() = runTest {
        val item = Item("title1", "link", "thumbnail", 0, 0)
        val mockWorkInfo = mock(WorkInfo::class.java)
        val mockOutputData = mock(Data::class.java)

        `when`(mockOutputData.getString(any())).thenReturn("test uri")
        `when`(mockWorkInfo.state).thenReturn(WorkInfo.State.SUCCEEDED)
        `when`(mockWorkInfo.outputData).thenReturn(mockOutputData)

        `when`(mockDownloadCenter.startDownload(eq(item), any())).thenAnswer{
            it.getArgument<(WorkInfo?)->Unit>(1).invoke(mockWorkInfo)
         }

        mainViewModel.downloadEventFlow.test {
            mainViewModel.downloadImage(item)
            val result = awaitItem()
            assertTrue(result is DownloadState.Complete)
            assertEquals("test uri", (result as DownloadState.Complete).uri)
        }
    }

    @Test
    @DisplayName("이미지 다운로드 시작")
    fun test_download_image_start() = runTest {
        val item = Item("title1", "link", "thumbnail", 0, 0)
        val mockWorkInfo = mock(WorkInfo::class.java)

        `when`(mockWorkInfo.state).thenReturn(WorkInfo.State.ENQUEUED)

        `when`(mockDownloadCenter.startDownload(eq(item), any())).thenAnswer{
            it.getArgument<(WorkInfo?)->Unit>(1).invoke(mockWorkInfo)
        }

        mainViewModel.downloadEventFlow.test {
            mainViewModel.downloadImage(item)
            val result = awaitItem()
            assertTrue(result is DownloadState.Start)
        }
    }

    @Test
    @DisplayName("이미지 다운로드 진행중")
    fun test_download_image_progress() = runTest {
        val item = Item("title1", "link", "thumbnail", 0, 0)
        val mockWorkInfo = mock(WorkInfo::class.java)
        val mockOutputData = mock(Data::class.java)

        `when`(mockOutputData.getInt(any(), eq(0))).thenReturn(50)
        `when`(mockWorkInfo.state).thenReturn(WorkInfo.State.RUNNING)
        `when`(mockWorkInfo.progress).thenReturn(mockOutputData)

        `when`(mockDownloadCenter.startDownload(eq(item), any())).thenAnswer{
            it.getArgument<(WorkInfo?)->Unit>(1).invoke(mockWorkInfo)
        }

        mainViewModel.downloadEventFlow.test {
            mainViewModel.downloadImage(item)
            val result = awaitItem()
            assertTrue(result is DownloadState.Progress)
            assertEquals(50, (result as DownloadState.Progress).progress)
        }
    }

    @Test
    @DisplayName("이미지 다운로드 실패")
    fun test_download_image_failure() = runTest {
        val item = Item("title1", "link", "thumbnail", 0, 0)
        val mockWorkInfo = mock(WorkInfo::class.java)

        `when`(mockWorkInfo.state).thenReturn(WorkInfo.State.FAILED)

        `when`(mockDownloadCenter.startDownload(eq(item), any())).thenAnswer{
            it.getArgument<(WorkInfo?)->Unit>(1).invoke(mockWorkInfo)
        }

        mainViewModel.downloadEventFlow.test {
            mainViewModel.downloadImage(item)
            val result = awaitItem()
            assertTrue(result is DownloadState.Fail)
        }
    }

    @Test
    @DisplayName("북마크 이미지 추가")
    fun test_add_bookmark() = runTest {
        val item = Item("title1", "link", "thumbnail", 0, 0)
        mainViewModel.handleBookmark(item, true).join()
        verify(mockRepository).insertBookmarkImage(item)
    }

    @Test
    @DisplayName("북마크 이미지 제거")
    fun test_remove_bookmark() = runTest {
        val item = Item("title1", "link", "thumbnail", 0, 0)
        mainViewModel.handleBookmark(item, false).join()
        verify(mockRepository).deleteBookmarkImage(item)
    }

    @Test
    @DisplayName("테스트")
    fun test_pr() = runTest {
        assertEquals(4, 2 + 2)
    }
}
