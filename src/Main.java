import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static final int ACCOUNTS = 100;
    public static final double BALANCE = 1000;
    public static final double MAX_AMOUNT = 1000;
    public static final int DELAY = 10;

    public static void main(String[] args) {
        Bank bank = new Bank(ACCOUNTS,BALANCE);
        for (int i = 0; i < ACCOUNTS; i++) {
            int fromAccount = i;
            Runnable r = ()->{
                try{
                    for (int j = 0; j < 100 ; j++)
                    {
                        int toAccount= (int) (bank.size()*Math.random());
                        double amount = MAX_AMOUNT * Math.random();
                        bank.transfer(fromAccount,toAccount,amount);
                        Thread.sleep((int)(DELAY* Math.random()));
                    }
                }
                catch (InterruptedException e){}
            };
            Thread t = new Thread(r);
            t.start();
        }
    }
}
class Bank
{
    private double[] accounts;
    private Lock banklock = new ReentrantLock();
    private Condition nieMaPieniedzy = banklock.newCondition();


    public Bank(int n, double initialBalance)
    {
        accounts = new double[n];
        Arrays.fill(accounts,initialBalance);
    }

    public void transfer(int from, int to, double amount)
    {
        banklock.lock();
        try {
            while (accounts[from] < amount)
                nieMaPieniedzy.await();
                //if (accounts[from]<amount) return;

                System.out.println(Thread.currentThread());
                accounts[from] -= amount;
                System.out.printf("%10.2f z %d na %d", amount, from, to);
                accounts[to] += amount;
                System.out.printf("Saldo ogolne: %10.2f%n", getTotalBalance());
                nieMaPieniedzy.signalAll();

        }
        catch (InterruptedException e ){}
        catch (NullPointerException n){}
        finally{
                banklock.unlock();
            }
    }

    public double getTotalBalance()
    {
        double sum = 0;
        for (double a : accounts)
            sum +=a;
        return sum;
    }
    public int size()
    {
        return accounts.length;
    }


}
