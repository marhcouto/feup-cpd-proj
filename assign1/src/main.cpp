#include <cstdio>
#include <fstream>
#include <iostream>
#include <ctime>
#include <cmath>
#include <cstdlib>
#include <papi.h>
#include "papi_macro.hpp"

using namespace std;

#define SYSTEMTIME clock_t

#define NUMBER_OF_TRIES 3
#define BLOCK_SIZE 512

int bkSize = BLOCK_SIZE; //TODO: Encontrar melhor forma de fazer isto

void OnMult(int m_ar, int m_br, double *timeRes = NULL, ostream& debugStream = cerr)
{
  SYSTEMTIME Time1, Time2;

  char st[100];
  double temp;
  int i, j, k;

  double *pha, *phb, *phc;

  pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
  phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
  phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

  for (i = 0; i < m_ar; i++)
    for (j = 0; j < m_ar; j++)
      pha[i * m_ar + j] = (double)1.0;

  for (i = 0; i < m_br; i++)
    for (j = 0; j < m_br; j++)
      phb[i * m_br + j] = (double)(i + 1);

  Time1 = clock();

  for (i = 0; i < m_ar; i++)
  {
    for (j = 0; j < m_br; j++)
    {
      temp = 0;
      for (k = 0; k < m_ar; k++)
      {
        temp += pha[i * m_ar + k] * phb[k * m_br + j];
      }
      phc[i * m_ar + j] = temp;
    }
  }

  Time2 = clock();

  // display 10 elements of the result matrix tto verify correctness
  for (i = 0; i < 1; i++)
  {
    for (j = 0; j < min(10, m_br); j++)
      debugStream << phc[j] << " ";
  }
  debugStream << ";";

  free(pha);
  free(phb);
  free(phc);

  if (timeRes != NULL)
  {
    *timeRes = (double)(Time2 - Time1) / CLOCKS_PER_SEC;
  }
}

// add code here for line x line matriz multiplication
void OnMultLine(int m_ar, int m_br, double *timeRes = NULL, ostream& debugStream = cerr)
{
  SYSTEMTIME Time1, Time2;

  char st[100];
  double temp;
  int i, j, k;

  double *pha, *phb, *phc;

  pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
  phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
  phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

  for (i = 0; i < m_ar; i++)
    for (j = 0; j < m_ar; j++)
      pha[i * m_ar + j] = (double)1.0;
  phc[i * m_ar + j] = (double)0.0;

  for (i = 0; i < m_br; i++)
    for (j = 0; j < m_br; j++)
      phb[i * m_br + j] = (double)(i + 1);

  Time1 = clock();

  for (i = 0; i < m_ar; i++)
  {
    for (j = 0; j < m_br; j++)
    {
      for (k = 0; k < m_br; k++)
      {
        phc[i * m_ar + k] += pha[i * m_ar + j] * phb[j * m_ar + k];
      }
    }
  }

  Time2 = clock();

  // display 10 elements of the result matrix tto verify correctness
  for (i = 0; i < 1; i++)
  {
    for (j = 0; j < min(10, m_br); j++)
      debugStream << phc[j] << ";";
  }
  debugStream << ';';

  free(pha);
  free(phb);
  free(phc);

  if (timeRes != NULL)
  {
    *timeRes = (double)(Time2 - Time1) / CLOCKS_PER_SEC;
  }
}

// add code here for block x block matriz multiplication
void OnMultBlock(int m_ar, int m_br, double *timeRes = NULL, ostream& debugStream = cerr)
{
  if (m_ar % bkSize != 0 || m_br % bkSize != 0)
  {
    return;
  }

  SYSTEMTIME Time1, Time2;

  char st[100];
  double temp;
  int i0, j0, k0, i, j, k, numberOfBlocks;

  double *pha, *phb, *phc;

  pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
  phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
  phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

  for (i = 0; i < m_ar; i++)
    for (j = 0; j < m_ar; j++)
      pha[i * m_ar + j] = (double)1.0;
  phc[i * m_ar + j] = (double)0.0;

  for (i = 0; i < m_br; i++)
    for (j = 0; j < m_br; j++)
      phb[i * m_br + j] = (double)(i + 1);

  numberOfBlocks = (m_ar / bkSize);

  Time1 = clock();

  for (i0 = 0; i0 < m_ar; i0 += bkSize)
  {
    for (j0 = 0; j0 < m_ar; j0 += bkSize)
    {
      for (k0 = 0; k0 < m_ar; k0 += bkSize)
      {
        for (i = i0; i < i0 + bkSize; i++)
        {
          for (j = j0; j < j0 + bkSize; j++)
          {
            for (k = k0; k < k0 + bkSize; k++)
            {
              phc[i * m_ar + k] += pha[i * m_ar + j] * phb[j * m_ar + k];
            }
          }
        }
      }
    }
  }

  Time2 = clock();

  // display 10 elements of the result matrix tto verify correctness
  for (i = 0; i < 1; i++)
  {
    for (j = 0; j < min(10, m_br); j++)
      debugStream << phc[j] << ";";
  }
  cout << ';';

  free(pha);
  free(phb);
  free(phc);

  if (timeRes != NULL)
  {
    *timeRes = (double)(Time2 - Time1) / CLOCKS_PER_SEC;
  }
}

void handle_error(int retval)
{
  printf("PAPI error %d: %s\n", retval, PAPI_strerror(retval));
  exit(1);
}

void init_papi(int *EventSet)
{
  int retval = PAPI_library_init(PAPI_VER_CURRENT);
  if (retval != PAPI_VER_CURRENT && retval < 0)
  {
    printf("PAPI library version mismatch!\n");
    exit(1);
  }
  if (retval < 0)
    handle_error(retval);

  std::cout << "PAPI Version Number: MAJOR: " << PAPI_VERSION_MAJOR(retval)
            << " MINOR: " << PAPI_VERSION_MINOR(retval)
            << " REVISION: " << PAPI_VERSION_REVISION(retval) << "\n";
  
  retval = PAPI_create_eventset(EventSet);
  if (retval != PAPI_OK)
    cout << "ERROR: create eventset" << endl;

  //TODO: Estudar efeitos dos eventos PAPI aqui

  retval = PAPI_add_event(*EventSet, PAPI_L1_DCM);
  if (retval != PAPI_OK)
    cout << "ERROR: PAPI_L1_DCM" << endl;

  retval = PAPI_add_event(*EventSet, PAPI_L2_DCM);
  if (retval != PAPI_OK)
    cout << "ERROR: PAPI_L2_DCM" << endl;
}

void destroy_papi(int EventSet) {
  int ret;
  ret = PAPI_remove_event(EventSet, PAPI_L1_DCM);
  if (ret != PAPI_OK)
    std::cout << "FAIL remove event" << endl;

  ret = PAPI_remove_event(EventSet, PAPI_L2_DCM);
  if (ret != PAPI_OK)
    std::cout << "FAIL remove event" << endl;

  ret = PAPI_destroy_eventset(&EventSet);
  if (ret != PAPI_OK)
    std::cout << "FAIL destroy" << endl;
}

void benchmark(string filePrefix, size_t initialSize, size_t finalSize, size_t step, int EventSet, void (*action)(int, int, double*, ostream&))
{
  long long eventValues[NUMBER_OF_PAPI_EVENTS];
  long long executionEventValues[NUMBER_OF_PAPI_EVENTS];
  char filename[256];
  for (size_t matrixSize = initialSize; matrixSize <= finalSize; matrixSize += step)
  {
    sprintf(filename, "%s_result_%ld.csv", filePrefix.c_str(), matrixSize);
    ofstream benchmarkFile(filename);
    benchmarkFile << "Iteration;Result Matrix;";
    for (size_t i = 0; i < NUMBER_OF_PAPI_EVENTS; i++ ) {
      benchmarkFile << "Event " << i << ';';
    }
    benchmarkFile << "Execution Time" << endl;
    double avgExecutionTime = 0;
    for(size_t i = 0; i < NUMBER_OF_PAPI_EVENTS; i++)
    {
      eventValues[i] = 0;
    }
    for (size_t exe = 0; exe < NUMBER_OF_TRIES; exe++) {
      benchmarkFile << exe << ';';
      double executionTime = 0;
      start_papi_event_counter(EventSet);
      action(matrixSize, matrixSize, &executionTime, benchmarkFile);
      stop_papi_event_counter(EventSet, executionEventValues);
      for (size_t i = 0; i < NUMBER_OF_PAPI_EVENTS; i++) {
        benchmarkFile << executionEventValues[i] << ';';
        eventValues[i] += executionEventValues[i];
      }
      benchmarkFile << executionTime << std::endl;
      avgExecutionTime += executionTime;
      reset_papi_event_counter(EventSet)
    }
    avgExecutionTime /= NUMBER_OF_TRIES;
    for (int i = 0; i < NUMBER_OF_PAPI_EVENTS; i++) {
      eventValues[i] = (long long) round(eventValues[i] / (double) NUMBER_OF_TRIES);
    }
    benchmarkFile << "Average;;";
    for (size_t i = 0; i < NUMBER_OF_PAPI_EVENTS; i++) {
      benchmarkFile << eventValues[i] << ";";
    }
    benchmarkFile << avgExecutionTime << std::endl;
    benchmarkFile.close();
  }
}

int main(int argc, char *argv[])
{

  char c;
  int lin, col, blockSize;
  int op;

  int EventSet = PAPI_NULL;
  long long values[2];
  
  init_papi(&EventSet);

  op = 1;
  do
  {
    cout << endl
         << "1. Multiplication" << endl;
    cout << "2. Line Multiplication" << endl;
    cout << "3. Block Multiplication" << endl;
    cout << "4. Benchmark Multiplication(600 to 3000 step 400)" << endl;
    cout << "5. Benchmark Line Multiplication(600 to 3000 step 400)" << endl;
    cout << "6. Benchmark Line Multiplication(4096 to 10240 step 2048)" << endl;
    cout << "7. Benchmark Block Multiplication(4096 to 10240 step 2048)" << endl;
    cout << "Selection?: ";
    cin >> op;
    if (op == 0)
      break;
    if (op < 4) {
      printf("Dimensions: lins=cols ? ");
      cin >> lin;
      col = lin;
    }

    switch (op)
    {
      case 1:
        OnMult(lin, col);
        break;
      case 2:
        OnMultLine(lin, col);
        break;
      case 3:
        cout << "Block Size? ";
        cin >> blockSize;
        bkSize = blockSize;
        OnMultBlock(lin, col);
        break;
      case 4:
        benchmark("mult", 600, 3000, 400, EventSet, OnMult);
        break;
      case 5:
        benchmark("mult_line", 600, 3000, 400, EventSet, OnMultLine);
        break;
      case 6:
        benchmark("mult_line", 4096, 10240, 2048, EventSet, OnMultLine);
        break;
      case 7:
        benchmark("mult_block", 4096, 10240, 2048, EventSet, OnMultBlock);
        break;
    }
  } while (op != 0);

  destroy_papi(EventSet);
}
