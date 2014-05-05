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

/**
 * Generates a permutation for the string
 */
std::vector<std::string> permutation(std::string text) {
  std::vector<std::string> result;
  if(text.size() == 0) {
    return result;
  }
   
  std::string prefix = text.substr(0,1);
  std::string remaining;
  if(text.size() > 1)
    remaining = text.substr(1, text.size() - 1);
  std::vector<std::string> permutations = permutation(remaining);
  for(int index = 0; index < permutations.size(); index++) {
    for(int pos = 0; pos <= permutations[index].size(); pos++) {
        std::string permt = permutations[index];
        permt.insert(pos, prefix);
        result.push_back(permt);    
    }
  }
  if(result.size() == 0)
    result.push_back(prefix);
  return result;
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
  result = permutation(test);
  printVector(result);
}
