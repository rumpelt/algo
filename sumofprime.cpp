#include <stdlib.h>
#include <iostream>
#include <time.h>
#include <math.h>

/**
 * author : ashwani
 * Given a number  less than equal to 10 000 000, this program will print
 * the sum of all prime numbers less than that number.
 * This speed of this program is optimal for numbers less than 10,000,000
 * To compile : g++ sumofprime.cpp
 * To execute : ./a.out 123445
 * Implementation based on https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes
 *
 */

/**
 * This is struct which maintain an arry of odd numbers which are potential prime numbers
 */
struct OddArray{
  long* primes; // Array of odd numbers , except the first number is 2;  These odd numbers are potential prime numbers.
  long number; // The number for which we want to calculate the sum of primes
  long size; // size of the array primes above.  This is required for pointer arthmetics 

  OddArray(long number) : number(number) {
    OddArray::size = number /2; // The number of odd numbers possibel. We allocate an array of possible primes of this size
    OddArray::primes  = new long[size];
    *(OddArray::primes) = 2;
    for(long index = 1; index < OddArray::size; index++) {
      *(OddArray::primes + index) = (2 * index + 1);   // We initialze the array of primes to odd number i.e. 2, 3, 5, 7, 9 , 11
    } 
  }
 
  ~OddArray() {
    delete[] OddArray::primes;
  }
 
  /**
   * If we discover the value at particulr index is not prime then we set it to -1.
   * Remember that we are storing an array of odd numbers and when we discover that an odd number is not prime then we set it to -1
   */
  void markBad(long index) {
    if(index < OddArray::size)
      *(OddArray::primes + index)  = -1;
  }

  
  /**
   * We can guess the index at which an number is stored in the array
   */
  long valueToIndex(long& value) {
    if(value == 2) // 2 is always stored at index 0
      return 0;
    else if((value %2 == 0)) // We never store an even number so return -1
      return -1; // return -1 for even numbers
    else
      return value /2 ; // This is odd number and stored at particular index..Look at the initialization of struct
  }
  
  /**
   * Return the sum of all the valid numbers in the array.
   * All numbers are odd and prime, rest are set to -1
   */
  long primeSum() {
    long sum = 0;
    for(long index = 0; index < OddArray::size; index++) {
      long value = *(OddArray::primes + index);
      if(value != -1)
        sum = sum + value;
    }
    return sum;
  }

  /**
   * Number must be greater than 1;
   * Check if an odd number is prime
   */
   bool oddPrimeCheck(long& value) {
     long sqrtMax = sqrt(value) + 1;
     for(long index=3; index <= sqrtMax; index = index + 2) {
       if((value % index) == 0)
        return false;
     }  
     return true;
   }

  /** 
   * This is implemenation of https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes
   */
  void performAlgorithm() {

    long sqrtMax = sqrt(OddArray::number)  + 1;
    for(long index = 1; index < OddArray::size; index++) {
      long value = OddArray::primes[index]; // Get the value stored in the array at this index
      if(value > sqrtMax)
        break;
      if(value != -1) {
        if(OddArray::oddPrimeCheck(value)) { // Check if the value is prime
	  for(long res = value * value; res < OddArray::number ; res = res + value) {
            long markIndex = OddArray::valueToIndex(res); // get the index where this number is stored and mark it to -1 as this is not a prime number
            if(markIndex >= 0)  
              *(OddArray::primes + markIndex) = -1;
	  }
	}
      }
    }
  }

};

bool crudePrimeCheck(long number) {
  if(number == 2)
    return  true;
  if((number % 2) == 0) 
    return false;
  long sqrtMax = sqrt(number) + 1;
  for(long index=3; index <= sqrtMax; index = index + 2) {
    if((number % index) == 0)
      return false;
  }  
  return true;
}

long naivePrimeSum(long maxNumber) {
  long primeSum = 0;
  for(int index = 2; index < maxNumber;  index++) {
    if(crudePrimeCheck(index))
      primeSum = primeSum + index;
  }
  return primeSum;
}



int main(int argc, char* argv[]) {
  //clock_t start = clock(); 
  long inputNumber;
  if (argc < 2) {
    std::cout << "Input number not specified .. Exiting" << std::endl;
    return -1;
  }

  inputNumber = atol(argv[1]); // The number for which we want to calculate the sum of primes
  OddArray odd(inputNumber); // Initialze the Array of Odd numbers
  odd.performAlgorithm(); // Perform the algorithm to find all the prime numbers
  std::cout << odd.primeSum() << std::endl; // calculate and print the sum of prime numbers
  
  //std::cout << naivePrimeSum(inputNumber) << std::endl; // A naive algorithm for sum of prime numbers for test purpose.
  //std::cout << "Total time taken " << ((double)(clock() - start) / CLOCKS_PER_SEC) << std::endl;
}
