# Coding Conventions for our WIZ Members:

- With `java`: [Oracle's Offical Documentation][1]

```java
package org.wiz.website.backend;

import java.util.List.*; // NOTE: Avoid importing all; prefer using the specification library. 
import java.util.Arrays;

public class Main {

  // NOTE: Using `Lombok` annotations instead of generating getter/setter/constructors.

  private static final Double NUMBER_CONST = 1000_000_000;

  private String firstProperty = "This is our global private property";
  private String secondProperty = "This is our global private property";

  public String thirdProperty = "This is our global public property";
  public String fourthProperty = "This is our global public property";

  static {
    Integer flag = 1000;
    NUMBER_CONST = 1000_000;
    System.out.println(printNumber(flag, NUMBER_CONST));
  }

  public String printNumber(Integer first, Double second) {
    return (Double.valueOf(first) < second) ? String.valueOf(first) : String.valueOf(second);
  }

  private List<String> convertToList(String input) {
    List<String> filteredList = Arrays.asList(input).stream()
                    .filter(s -> Integer.valueOf(s) > 0)
                    .collect(Collection.toList());

    return filteredList;
  }

  public static void main(String[] args) {
    System.out.println("WIZ Team say nothing but action!");

    String afterFiltered = convertToList(firstProperty);

    if ((firstProperty == secondProperty) && (afterFiltered.length > 0)) {
      System.out.println("Multi-conditions' convention");
    }
  }

}
```

[1]: https://www.oracle.com/java/technologies/javase/codeconventions-programmingpractices.html
