package com.tarot.demo.coupon;

import com.tarot.demo.DTO.CouponIssueMessage;
import com.tarot.demo.config.CouponKafkaConsumer;
import com.tarot.demo.coupon.mapper.CouponMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class CouponKafkaConsumerTest {
    private final CouponMapper mapper = mock(CouponMapper.class);
    private final PlatformTransactionManager manager = mock(PlatformTransactionManager.class);
    private final TransactionStatus status = mock(TransactionStatus.class);
    private final CouponIssueMessage message = new CouponIssueMessage("test000001", "C001");
    private final CouponKafkaConsumer consumer = new CouponKafkaConsumer(mapper, manager);

    @BeforeEach
    void setup() {
        when(manager.getTransaction(any())).thenReturn(status);
        when(mapper.lockCoupon("C001")).thenReturn("C001");
        when(mapper.updateCouponStock("C001")).thenReturn(1);
    }

    @Test
    void locksParentBeforeInsertAndCommits() {
        consumer.consume(message);
        var order = inOrder(mapper, manager);
        order.verify(mapper).lockCoupon("C001");
        order.verify(mapper).countCoupon(any());
        order.verify(mapper).updateCouponStock("C001");
        order.verify(mapper).coupon(any());
        order.verify(manager).commit(status);
    }

    @Test
    void duplicateDoesNotDecreaseStockEvenWhenSoldOut() {
        when(mapper.countCoupon(any())).thenReturn(1);
        when(mapper.updateCouponStock("C001")).thenReturn(0);
        consumer.consume(message);
        verify(mapper, never()).updateCouponStock(any());
        verify(mapper, never()).coupon(any());
        verify(manager).commit(status);
    }

    @Test
    void insertFailureRollsBackAndEscapesForKafkaRetry() {
        var failure = new DataIntegrityViolationException("insert failed");
        doThrow(failure).when(mapper).coupon(any());
        assertThatThrownBy(() -> consumer.consume(message)).isSameAs(failure);
        verify(manager).rollback(status);
        verify(manager, never()).commit(any());
    }

    @Test
    void deadlockRollsBackAndNextDeliveryStartsAnotherTransaction() {
        when(mapper.updateCouponStock("C001"))
            .thenThrow(new CannotAcquireLockException("deadlock")).thenReturn(1);
        assertThatThrownBy(() -> consumer.consume(message)).isInstanceOf(CannotAcquireLockException.class);
        consumer.consume(message);
        verify(manager, times(2)).getTransaction(any());
        verify(manager).rollback(status);
        verify(manager).commit(status);
        verify(mapper).coupon(any());
    }

    @Test
    void missingStockDoesNotInsertAndRollsBack() {
        when(mapper.updateCouponStock("C001")).thenReturn(0);
        assertThatThrownBy(() -> consumer.consume(message)).isInstanceOf(IllegalStateException.class);
        verify(mapper, never()).coupon(any());
        verify(manager).rollback(status);
    }
}
