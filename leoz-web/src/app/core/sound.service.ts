import { Injectable } from '@angular/core';

@Injectable()
export class SoundService {

  public soundTypes = [ 'chord', 'critical', 'ding', 'remove' ];
  sounds = {};

  constructor() {
    this.loadSoundSources();
  }

  private loadSoundSources() {
    this.soundTypes.forEach( ( type: string ) => this.sounds[ type ] = this.load( type ) );
  }

  private load( type: string ) {
    const audio = new Audio();
    audio.src = `assets/sounds/${type}.wav`;
    audio.load();
    return audio;
  }

  play( type: string ) {
    if (this.sounds[ type ]) {
      this.sounds[ type ].play();
    }
  }
}
