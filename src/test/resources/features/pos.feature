Feature: Get discounted products from Myntra based on gender, type, and brand

  Scenario Outline: Find discounted <type> for <brand> under <gender> category
    Given I open the Myntra website at "<links>"
    When I select "<gender>" as the gender
    And I choose "<type>" as the product category
    And I apply the brand filter for "<brand>"
    Then I collect the products that are on discount from maximum <mx> pages
    And I sort those products based on discount in descending order
    And I print the final product list to the console

  Examples:
    | brand       | gender | type    | links                                                               | mx |
    | Van Heusen  | Men    | T-shirts | https://www.myntra.com                                 | 2  |
    | Roadster  | Men  | Casual Shirts    | https://www.myntra.com   | 2  |
