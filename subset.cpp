#include <vector>
#include <string>
#include <iostream>

/**
 * Generates all the possible subsets of a string. Memory intensive as subsets are pushed back
 * on the result vector.
 * input e.g : abc
 * result vector contain following
 * a , b , c, ab , ac, abc
 */
void subsets(std::string input, std::vector<std::string>& result) {
  if(input.size() <= 0)
    return;
  std::string first = input.substr(0,1);
  std::string remaining;
  if(input.size() > 1)
    remaining = input.substr(1, input.size() - 1);
  subsets(remaining, result);

  std::vector<std::string> combination;
  for(std::vector<std::string>::iterator it  = result.begin(); it != result.end(); ++it) {
    std::string toadd = first + *it;
    combination.push_back(toadd);
  }

  result.insert(result.end(), combination.begin(), combination.end());
  result.push_back(first);
}


void printVector(std::vector<std::string> vec) {
  for(std::vector<std::string>::iterator it = vec.begin(); it != vec.end(); ++it) {
    std::cout << *it << std::endl;
  }
  std::cout << "subset size " << vec.size() << std::endl;
}

int main(int argc, char* argv[])  {
  std::string test(argv[1]);
  std::vector<std::string> result;
  std::cout << "Generating Subset of string " << test << std::endl;
  subsets(test, result);
  printVector(result);
}
