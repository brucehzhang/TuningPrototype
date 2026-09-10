package com.tuning.tuningprototype.models.mappers.data.entity;

import com.tuning.tuningprototype.models.db.entity.Account;
import com.tuning.tuningprototype.models.db.entity.AccountDto;
import com.tuning.tuningprototype.models.db.entity.User;
import com.tuning.tuningprototype.models.db.entity.UserDto;
import com.tuning.tuningprototype.testutil.UninitializedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountMapperTest {

    @Mock
    private UserMapper userMapper;

    private AccountMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AccountMapper(userMapper);
    }

    @Test
    void toDto_returnsNull_whenEntityIsNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_mapsScalarFields_andUsers_whenInitialized() {
        User user = User.builder().id(9L).build();
        UserDto userDto = new UserDto(
                9L, "Jane", null, null, "jdoe", null, null, 1L, null, null, null, null);
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<User> users = new ArrayList<>();
        users.add(user);

        Account entity = Account.builder()
                .id(1L)
                .name("Acme Corp")
                .createdTime(2000L)
                .modifiedTime(3000L)
                .users(users)
                .build();

        AccountDto dto = mapper.toDto(entity);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Acme Corp");
        assertThat(dto.createdTime()).isEqualTo(2000L);
        assertThat(dto.modifiedTime()).isEqualTo(3000L);
        assertThat(dto.users()).containsExactly(userDto);
    }

    @Test
    void toDto_usersIsNull_whenCollectionUninitialized() {
        Account entity = Account.builder()
                .id(1L)
                .users(new UninitializedList<>())
                .build();

        AccountDto dto = mapper.toDto(entity);

        assertThat(dto.users()).isNull();
        verifyNoInteractions(userMapper);
    }

    @Test
    void toEntity_returnsNull_whenDtoIsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        AccountDto dto = new AccountDto(1L, "Acme Corp", 2000L, 3000L, List.of());

        Account entity = mapper.toEntity(dto);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Acme Corp");
        assertThat(entity.getCreatedTime()).isEqualTo(2000L);
        assertThat(entity.getModifiedTime()).isEqualTo(3000L);
    }
}
