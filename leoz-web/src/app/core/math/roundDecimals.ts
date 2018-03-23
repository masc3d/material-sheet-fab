export function roundDecimals( input: number, decimal: number = 10): number {
  return Math.round( (input) * decimal ) / decimal;
}
export function roundDecimalsAsString( input: number, decimal: number = 10, useEmptyStringAsZero: boolean = false ): string {
  const result = roundDecimals( input, decimal);
  if (useEmptyStringAsZero && result <= 0) {
    return '';
  }
  return result.toString();
}
