package com.xksms.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisHelperTest {

    private static final String KEY = "demo";
    private static final String LOCK_KEY = "lock";
    private static final String REQUEST_ID = "request";

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private RedisHelper redisHelper;

    @BeforeEach
    void setUp() {
        redisHelper = new RedisHelper(redisTemplate);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void getReturnsEmptyWhenValueTypeDoesNotMatchRequestedType() {
        when(valueOperations.get(KEY)).thenReturn(42L);

        Optional<String> result = redisHelper.get(KEY, String.class);

        assertThat(result).isEmpty();
        verify(valueOperations).get(KEY);
    }

    @Test
    void releaseLockUsesLuaScriptAndReturnsTrueWhenKeyIsDeleted() {
        when(redisTemplate.execute(any(RedisScript.class), anyList(), any())).thenReturn(1L);

        boolean released = redisHelper.releaseLock(LOCK_KEY, REQUEST_ID);

        assertThat(released).isTrue();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<String>> keysCaptor = ArgumentCaptor.forClass(List.class);
        verify(redisTemplate).execute(any(RedisScript.class), keysCaptor.capture(), eq(REQUEST_ID));
        assertThat(keysCaptor.getValue()).containsExactly(LOCK_KEY);
    }

    @Test
    void releaseLockReturnsFalseWhenRedisDoesNotDeleteKey() {
        when(redisTemplate.execute(any(RedisScript.class), anyList(), any())).thenReturn(0L);

        boolean released = redisHelper.releaseLock(LOCK_KEY, REQUEST_ID);

        assertThat(released).isFalse();
    }
}
