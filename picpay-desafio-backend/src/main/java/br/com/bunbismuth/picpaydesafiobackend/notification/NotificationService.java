package br.com.bunbismuth.picpaydesafiobackend.notification;

import br.com.bunbismuth.picpaydesafiobackend.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
  private final NotificationProducer notificationProducer;

  public NotificationService(NotificationProducer notificationProducer) {
    this.notificationProducer = notificationProducer;
  }

  public void notify(Transaction transaction) {
    LOGGER.info("notifying transaction {}...", transaction);

    notificationProducer.sendNotification(transaction);
  }
}
