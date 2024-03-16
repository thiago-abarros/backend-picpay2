package br.com.bunbismuth.picpaydesafiobackend.transaction;

import br.com.bunbismuth.picpaydesafiobackend.wallet.WalletRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);
  private final TransactionRepository transactionRepository;
  private final WalletRepository walletRepository;

  public TransactionService(TransactionRepository transactionRepository,
      WalletRepository walletRepository) {
    this.transactionRepository = transactionRepository;
    this.walletRepository = walletRepository;
  }

  @Transactional
  public Transaction create(Transaction transaction) {

    var newTransaction = transactionRepository.save(transaction);

    var walletPayer = walletRepository.findById(transaction.payer()).get();
    var wallerPayee = walletRepository.findById(transaction.payee()).get();
    walletRepository.save(walletPayer.debit(transaction.value()));
    walletRepository.save(wallerPayee.credit(transaction.value()));

    return newTransaction;
  }

  public List<Transaction> list() {
    return transactionRepository.findAll();
  }
}
