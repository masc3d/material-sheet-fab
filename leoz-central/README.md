# Jersey/spring bridge #

* @Inject/@Autowired works fine within web service classes, but anything AOP related (eg @Transactional) ONLY works
within injected instances but not the webservice class itself.
there's a number of bugs on those issues eg. https://java.net/jira/browse/JERSEY-2112
Workaround: don't use anything @Inject within webservice classes, use other spring features only in injected classes
