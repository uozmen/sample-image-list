package com.example.ugurozmen.imagelist.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ugurozmen.imagelist.model.Photo
import com.example.ugurozmen.imagelist.model.PhotoRepository
import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verifyBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.io.IOException

class MainViewModelTest {
    @get:Rule
    var instantExecutorRule: TestRule = InstantTaskExecutorRule()

    private val itemsCount = 20
    private val errorDescription = "Error description"

    @ExperimentalCoroutinesApi
    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `When initialized Then content is Loaded`() {
        val tested = mainViewModel()
        tested.listItems.observeForever {}
        tested.content.observeForever {}

        Truth.assertThat(tested.content.value).isEqualTo(MainViewModel.Content.Loaded)
    }

    @Test
    fun `When initialized Then listItems are populated`() {
        val tested = mainViewModel()
        tested.listItems.observeForever {}
        tested.content.observeForever {}

        Truth.assertThat(tested.listItems.value).containsExactlyElementsIn(createItems())
    }

    @Test
    fun `When IO exception occurs Then content is Error`() {
        val photoRepository = photoRepository(ioExceptionOnQueryNumber = 0)
        val tested = mainViewModel(photoRepository)
        tested.listItems.observeForever {}
        tested.content.observeForever {}

        Truth.assertThat(tested.content.value)
            .isEqualTo(MainViewModel.Content.Error(errorDescription))
        verifyBlocking(photoRepository, times(1)) { getPhotos() }
    }

    @Test
    fun `Given IO exception is occurred When retry is clicked Then api is queried again`() {
        val photoRepository = photoRepository(ioExceptionOnQueryNumber = 0)
        val tested = mainViewModel(photoRepository)
        tested.listItems.observeForever {}
        tested.content.observeForever {}

        tested.onRetryClicked()

        Truth.assertThat(tested.content.value).isEqualTo(MainViewModel.Content.Loaded)
        verifyBlocking(photoRepository, times(2)) { getPhotos() }
    }

    @Test
    fun `When an item is selected Then Detail image is displayed`() {
        val selectedIndex = 2
        val expected = createDetail(selectedIndex)
        val tested = mainViewModel()
        tested.listItems.observeForever {}
        tested.content.observeForever {}

        tested.onItemSelected(selectedIndex)

        Truth.assertThat(tested.content.value).isEqualTo(expected)
    }

    @Test
    fun `Given an item is selected When back pressed Then Detail image is displayed`() {
        val selectedIndex = 2
        val tested = mainViewModel()
        tested.listItems.observeForever {}
        tested.content.observeForever {}
        tested.onItemSelected(selectedIndex)

        tested.onBackPressed()

        Truth.assertThat(tested.content.value).isEqualTo(MainViewModel.Content.Loaded)
    }

    private fun mainViewModel(photoRepository: PhotoRepository = photoRepository()) =
        MainViewModel(photoRepository)

    private fun photoRepository(ioExceptionOnQueryNumber: Int = Int.MAX_VALUE) =
        mock<PhotoRepository> {
            var queryNumber = 0
            onBlocking { getPhotos() } doAnswer {
                if (queryNumber++ == ioExceptionOnQueryNumber) {
                    throw IOException(errorDescription)
                }
                createPhotos()
            }
        }

    private fun createPhotos() = IntRange(0, itemsCount - 1).map {
        Photo(1, it.toLong(), "title $it", "original $it", "preview $it")
    }

    private fun createItems(): List<MainViewModel.Item> = IntRange(0, itemsCount - 1).map {
        MainViewModel.Item(
            "preview $it",
            "title $it"
        )
    }

    private fun createDetail(index: Int) =
        MainViewModel.Content.Detail("original $index", index, "title $index")
}