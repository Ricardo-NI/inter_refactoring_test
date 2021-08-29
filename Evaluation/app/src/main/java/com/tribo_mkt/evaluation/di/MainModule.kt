package com.tribo_mkt.evaluation.di

import com.tribo_mkt.evaluation.data.APIRepository
import com.tribo_mkt.evaluation.data.APIService
import com.tribo_mkt.evaluation.data.Repository
import com.tribo_mkt.evaluation.ui.albums.AlbumsViewModel
import com.tribo_mkt.evaluation.ui.comments.CommentsViewModel
import com.tribo_mkt.evaluation.ui.home.HomeViewModel
import com.tribo_mkt.evaluation.ui.photos.PhotosViewModel
import com.tribo_mkt.evaluation.ui.posts.PostsViewModel
import com.tribo_mkt.evaluation.utils.Util
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val mainModule = module {

    single {
        Retrofit.Builder()
            .baseUrl(Util.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single { get<Retrofit>().create(APIService::class.java)}

    single {
        APIRepository(get()) as Repository
    }

    viewModel { HomeViewModel(get()) }
    viewModel { PostsViewModel(get()) }
    viewModel { CommentsViewModel(get()) }
    viewModel { AlbumsViewModel(get()) }
    viewModel { PhotosViewModel(get()) }

}

