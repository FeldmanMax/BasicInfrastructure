package services

import org.scalatest.FunSuite

class TerminalCommanderTest extends FunSuite {
  test("pwd returns") {
    val terminalCommander: TerminalCommander = new TerminalCommander
    terminalCommander.run("pwd", (response: String) => {
      if(!response.contains("BaseInfrastructure")) Left(s"Doesn't contain service in the path \n $response")
      else                              Right("All Good")
    }) match {
      case Left(left) => fail(left.toString)
      case Right(value) => assert(value == "All Good")
    }
  }

  test("pws which returns an exception") {
    val terminalCommander: TerminalCommander = new TerminalCommander
    terminalCommander.run("pws", (response: String) => { Right(response) }) match {
      case Left(left) => assert(left.toString.contains("Cannot run program \"pws\": error=2, No such file or directory"))
      case Right(_) => fail("Did not throw an exception")
    }
  }
}
