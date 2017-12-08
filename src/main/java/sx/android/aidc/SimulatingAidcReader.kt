package sx.android.aidc

import sx.aidc.SymbologyType

/**
 * Created by masc on 17.07.17.
 */
class SimulatingAidcReader : AidcReader() {
    /**
     * Emits a synthetic aidc read evnet
     */
    fun emit(data: String, symbologyType: SymbologyType) {
        this.readEventSubject.onNext(ReadEvent(data = data, symbologyType = symbologyType))
    }
}