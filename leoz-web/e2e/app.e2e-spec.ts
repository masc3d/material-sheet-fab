import { JunkPage } from './app.po';

describe('junk App', () => {
  let page: JunkPage;

  beforeEach(() => {
    page = new JunkPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
