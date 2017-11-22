package com.eelly.net;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class Test<MovieEntity> {


    private void onR(CompositeDisposable vCompositeDisposable, Observable<MovieEntity> observable, Consumer<MovieEntity> consumer,
                     Consumer<Throwable> consumer1 ){
        vCompositeDisposable.add(observable.observeOn(Schedulers.io()).
                subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<MovieEntity>() {
            @Override
            public void accept(MovieEntity entity) throws Exception {
                entity.hashCode();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                System.out.print(throwable.getMessage().toString());
            }
        }));
    }
}
