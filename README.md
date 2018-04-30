# VAT Returns Microservice

[![Build Status](https://travis-ci.org/hmrc/vat-returns.svg)](https://travis-ci.org/hmrc/vat-returns)
[![Download](https://api.bintray.com/packages/hmrc/releases/vat-returns/images/download.svg) ](https://bintray.com/hmrc/releases/vat-returns/_latestVersion)

## Summary
This protected microservice provides a backend for Making Tax Digital for Businesses (MTDfB) VAT frontend services to retrieve VAT Returns for a user enrolled for MTD VAT.

## Running the application
To run this microservice, you must have SBT installed. You should then be able to start the application using:
                                       
```sbt run```

### Testing
```sbt test it:test```

### Requirements
This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), so needs at least a [JRE](https://www.java.com/en/download/) to run.

## License
This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)