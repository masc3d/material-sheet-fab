export function checkdigitInt25( input: string): number {
  let oddSum = 0;
  let evenSum = 0;
  let counter = 1;

  input
    .split( '' )
    .reverse()
    .forEach( ( char: string ) => {
      if (counter % 2 === 0) {
        evenSum += parseInt( char, 10 );
      } else {
        oddSum += parseInt( char, 10 );
      }
      counter += 1;
    } );
  /*
   * Modulo 10 von (Summe aus iOddsum*3 und iEvenSum ) von 10 abziehen,
   * Modulo 10 des Ergebnisses ist die Prüfziffer der ersten 9 Stellen
   */
  return (10 - ( ( (oddSum * 3) + evenSum) % 10) ) % 10;
}
