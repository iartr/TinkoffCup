package com.artr.tinkoffcup.mvvm

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import com.artr.tinkoffcup.design.R
import com.github.terrakok.cicerone.Router

open class BaseViewModel(
    open val router: Router = Router()
) : ViewModel() {
    protected val disposables = CompositeDisposable()

    private val isProgressDialogVisibleMutable: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    val isProgressDialogVisible: Observable<Boolean> = isProgressDialogVisibleMutable.distinctUntilChanged()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    protected fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
        compositeDisposable.add(this)
    }

    protected fun <T : Any> Single<T>.withProgressDialog(): Single<T> {
        return doOnSubscribe { isProgressDialogVisibleMutable.onNext(true) }
            .doFinally { isProgressDialogVisibleMutable.onNext(false) }
    }

    protected fun <T : Any> Observable<T>.withProgressDialog(): Observable<T> {
        return doOnSubscribe { isProgressDialogVisibleMutable.onNext(true) }
            .doFinally { isProgressDialogVisibleMutable.onNext(false) }
    }

    protected fun Completable.withProgressDialog(): Completable {
        return doOnSubscribe { isProgressDialogVisibleMutable.onNext(true) }
            .doFinally { isProgressDialogVisibleMutable.onNext(false) }
    }

    protected fun <T : Any> Single<T>.withErrorDisplay(@StringRes stringRes: Int): Single<T> {
        return doOnError { /* show toast */ }
    }

    protected fun <T : Any> Observable<T>.withErrorDisplay(@StringRes stringRes: Int): Observable<T> {
        return doOnError { /* show toast */ }
    }

    protected fun Completable.withErrorDisplay(@StringRes stringRes: Int ): Completable {
        return doOnError { /* show toast */ }
    }
}