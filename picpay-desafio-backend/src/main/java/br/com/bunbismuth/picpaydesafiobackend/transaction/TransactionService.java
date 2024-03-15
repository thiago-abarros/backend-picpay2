package br.com.bunbismuth.picpaydesafiobackend.transaction;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);
  private final TransactionRepository transactionRepository;

  public TransactionService(TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }

  public List<Transaction> list() {
    return transactionRepository.findAll();
  }

}
