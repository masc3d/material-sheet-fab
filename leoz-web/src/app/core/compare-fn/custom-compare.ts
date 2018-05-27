export function compareCustom( sortOrder: number, value1: any, value2: any ) {
  if (value1 == null && value2 != null) {
    return -1 * sortOrder;
  }
  if (value1 != null && value2 == null) {
    return sortOrder;
  }
  if (value1 == null && value2 == null) {
    return 0;
  }
  if (typeof value1 === 'string' && typeof value2 === 'string') {
    return value1.localeCompare( value2 ) * sortOrder;
  }
  const result = (value1 < value2) ? -1 : (value1 > value2) ? 1 : 0;
  return (sortOrder * result);
}
