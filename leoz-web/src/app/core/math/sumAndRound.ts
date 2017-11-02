export function sumAndRound( input: number[], digits = 1) {
  const digitFactor = digits * 10;
  return Math.round( input.reduce( ( a, b ) => a + b, 0 ) * digitFactor ) / digitFactor;
}
