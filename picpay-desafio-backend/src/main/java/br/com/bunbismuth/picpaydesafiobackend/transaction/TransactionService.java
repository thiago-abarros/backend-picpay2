package br.com.bunbismuth.picpaydesafiobackend.transaction;

import br.com.bunbismuth.picpaydesafiobackend.authorization.AuthorizerService;
import br.com.bunbismuth.picpaydesafiobackend.notification.NotificationService;
import br.com.bunbismuth.picpaydesafiobackend.wallet.WalletRepository;
import br.com.bunbismuth.picpaydesafiobackend.wallet.WalletType;
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
  private final NotificationService notificationService;
  private final AuthorizerService authorizerService;

  public TransactionService(TransactionRepository transactionRepository,
      WalletRepository walletRepository, NotificationService notificationService,
      AuthorizerService authorizerService) {
    this.transactionRepository = transactionRepository;
    this.walletRepository = walletRepository;
    this.notificationService = notificationService;
    this.authorizerService = authorizerService;
  }

  @Transactional
  public Transaction create(Transaction transaction) {
    validate(transaction);

    var newTransaction = transactionRepository.save(transaction);

    var walletPayer = walletRepository.findById(transaction.payer()).get();
    var walletPayee = walletRepository.findById(transaction.payee()).get();
    walletRepository.save(walletPayer.debit(transaction.value()));
    walletRepository.save(walletPayee.credit(transaction.value()));

    authorizerService.authorize(transaction);
    notificationService.notify(newTransaction);

    return newTransaction;
  }

  /*
   * A transaction is valid if:
   * - the payer is a common wallet
   * - the payer has enough balance
   * - the payer is not the payee
   */
  private void validate(Transaction transaction) {
    LOGGER.info("validating transaction {}...", transaction);

    walletRepository.findById(transaction.payee())
        .map(payee -> walletRepository.findById(transaction.payer())
            .map(
                payer -> payer.type() == WalletType.COMUM.getValue() &&
                    payer.balance().compareTo(transaction.value()) >= 0 &&
                    !payer.id().equals(transaction.payee()) ? true : null)
            .orElseThrow(() -> new InvalidTransactionException(
                "Invalid transaction - " + transaction)))
        .orElseThrow(() -> new InvalidTransactionException(
            "Invalid transaction - " + transaction));
  }

  public List<Transaction> list() {
    return transactionRepository.findAll();
  }

}
