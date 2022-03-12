from time import process_time



def partiallyPrintMatrix(matrix):

    print("Result (partial):\n")

    n = min(len(matrix), 6)

    for i in range(0,n):
        print('|', end="")
        for j in range(0,n):
            print(" {} ".format(matrix[i][j]), end="")
        print('|')
        
        

def OnMult(n):

    m1 = list()
    m2 = list()
    res = list()

    for i in range(0, n):
        m1.append([1] * n)
        m2.append([i + 1] * n)
        res.append([0] * n)

    for i in range(0, n):
        for j in range(0, n):
            for k in range(0, n):
                res[i][j] += m1[i][k] * m2[k][j]
    
    return res



def OnMultLine(n):

    m1 = list()
    m2 = list()
    res = list()

    for i in range(0, n):
        m1.append([1] * n)
        m2.append([i + 1] * n)
        res.append([0] * n)

    for i in range(0, n):
        for k in range(0, n):
            for j in range(0, n):
                res[i][j] += m1[i][k] * m2[k][j]
    
    return res



def OnMultBlock(n):

    m1 = list()
    m2 = list()
    res = list()

    for i in range(0, n):
        m1.append([1] * n)
        m2.append([i + 1] * n)
        res.append([0] * n)

    for i in range(0, n):
        for k in range(0, n):
            for j in range(0, n):
                res[i][j] += m1[i][k] * m2[k][j]
    
    return res
    


if __name__ == '__main__':

    while True:
        while True:
            option = int(input("1. Multiplication\n2. Line Multiplication\n3. Block Multiplication\n"))
            if option not in [1,2,3]:
                print("Invalid option (choose between 1 2 or 3)\n")
            else:
                break
            
        size = int(input('Matrix dimensions:\n'))
        
        if option == 1:
            start = process_time()
            res = OnMult(size)
            stop = process_time()
        elif option == 2:
            start = process_time()
            res = OnMultLine(size)
            stop = process_time()

        partiallyPrintMatrix(res)
        print("Time elapsed: {}".format(stop - start))

    