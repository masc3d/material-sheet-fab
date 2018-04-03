export const DATEFORMATS = {
  internal: {
    database: 'yy-MM-dd',
    en: 'yy/MM/dd',
    de: 'dd.MM.yyyy'
  },
  internalShort: {
    database: 'YYYY-MM-DD',
    en: 'YYYY/MM/DD',
    de: 'DD.MM.YYYY'
  },
  internalShorter: {
    database: 'YYYY-MM-DD',
    en: 'MM/dd',
    de: 'dd.MM.'
  },
  internalMedium: {
    database: 'MM-dd HH:mm',
    en: 'MM/dd HH:mm',
    de: 'dd.MM. HH:mm'
  },
  internalLong: {
    // database: 'yy-MM-dd HH:mm:ss', without momentJs
    // en: 'yy/MM/dd HH:mm',
    // de: 'dd.MM.yy HH:mm'
    database: 'YYYY-MM-DD HH:mm:ss',
    en: 'YYYY/MM/DD HH:mm',
    de: 'DD.MM.YYYY HH:mm'
  },
  internalLonger: {
    // database: 'yy-MM-dd HH:mm:ss', without momentJs
    // en: 'yy/MM/dd HH:mm:ss',
    // de: 'dd.MM.yy HH:mm:ss'
    database: 'YYYY-MM-DD HH:mm:ss',
    en: 'YYYY/MM/DD HH:mm:ss',
    de: 'DD.MM.YYYY HH:mm:ss'
  },
  primeng: {
    database: 'yy-mm-dd',
    en: 'yy/mm/dd',
    de: 'dd.mm.yy'
  }
};
