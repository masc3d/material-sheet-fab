export function roundDecimals( input: number, decimal: number = 10 ) {
  return Math.round( (input) * decimal ) / decimal;
}
