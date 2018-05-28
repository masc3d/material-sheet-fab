package sx.android.aidc

import io.reactivex.disposables.CompositeDisposable

/**
 * Composite aidc reader
 * @param readers AidcReaders for composition
 * Created by masc on 06/03/2017.
 */
class CompositeAidcReader(vararg readers: AidcReader) : AidcReader() {

    val readers: List<AidcReader>

    private var readerBindings = CompositeDisposable()

    init {
        this.readers = readers.toList()

        this.enabledProperty
                .distinctUntilChanged()
                .subscribe { update ->
                    readers.forEach { it.enabled = update.value }
                }

        this.decodersUpdatedSubject
                .distinctUntilChanged()
                .subscribe { decoders ->
                    readers.forEach { it.decoders.set(*decoders) }
                }

        readers.forEach {
            it.readEvent.subscribe {
                this.readEventSubject.onNext(it)
            }
        }
    }

    override fun onBind() {
        readerBindings.addAll(
                *this.readers.map { it.bind() }.toTypedArray()
        )
    }

    override fun onUnbind() {
        readerBindings.clear()
    }
}