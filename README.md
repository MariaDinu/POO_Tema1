//Dinu Maria-Beatrice 344C2

                                Tema1 - J. POO Morgan Chase & Co.

Main:
Incep in clasa Main prin a popula obiectele cu care voi lucra, cu informatiile din input:
- userii sistemului
- exchange rate-urile
↓
BankingSystem:
De aici se trece in clasa BankingSystem, in metoda doCommands care face actiunile necesare pentru
fiecare comanda cu ajutorul metodelor din aceeasi clasa unde se construieste si Json-ul
pentru output, se adauga tranzactii in istoric si se fac si schimbarile adecvate asupra obiectelor.
↓
BankingSystemTransactions si UserHistoryTransactions:
Am vrut sa separ tranzactiile ce se folosesc pentru output de tranzactiile ce se adauga in istoricul
userilor/conturilor, astfel le-am impartit in 2 clase. Acestea creeaza tranzactii si le adauga in
output/istoric.
↓
PaymentStrategy, PaymentSystem, OnlinePayment, TransferPayment si SplitPayment:
M-am folosit de design pattern-ul Strategy pentru putea face schimbarile necesare pentru fiecare tip
de plata (cu cardul, transfer sau split). Acestea creeaza si tranzactiile pentru plati care sunt adaugate
adecvat in istoric.
↓
CurrencyConverter si ExchangeRate:
Aici se calculeaza schimbarile valutare intre diverse monede (obiectele exhange rate). Ele fiind reprezentate
de un graf; nodurile reprezinta monedele, iar muchiile contin ratele de schimb intre acestea. Calculeaza rata
de schimb dintre doua monede fie direct, fie prin tranzactii intremediare.
↓
User, Account si Card:
Toate obiectele cu care lucram sunt ori useri, ori conturi, ori carduri. Card si Account sunt de tipul
abstract deocarece exista mai multe tipuri de carduri (classic si OneTimePay) si mai multe tipuri de
conturi (classic si savings), astfel doresc sa utilizes subtipurile necesare printr-o interfata comuna.
↓
CardFactory si AccountFactory:
Sunt folosite pentru a genera diferitele instante de carduri si conturi.

Design Patterns:
Am folosit pattern-ul Singleton pentru a avea o singura instanta a clasei BankingSystem, am mai folosit
pattern-ul Factory pentru a crea tipurile individuale de carduri/conturi, si am mai folosit pattern-ul
Strategy pentru a efectua plati specifice.

