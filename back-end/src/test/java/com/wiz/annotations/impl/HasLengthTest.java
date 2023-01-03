package com.wiz.annotations.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import com.wiz.exceptions.AnnotationException;
import com.wiz.utils.ValidateAnnotationUtils;

public class HasLengthTest {
    @Test
    void entity_has_null_list_must_throw_AnnotationException() {
        HasLengthEntity entity = new HasLengthEntity();

        var expected = AnnotationException.class;

        Executable actual = new Executable() {

            @Override
            public void execute() throws Throwable {
                ValidateAnnotationUtils.validate(entity);
            }

        };

        assertThrows(expected, actual);
    }

    @Test
    void entity_has_list_size_0_must_throw_AnnotationException() {
        HasLengthEntity entity = new HasLengthEntity();
        entity.setList(new ArrayList<>());

        var expected = AnnotationException.class;

        Executable actual = new Executable() {

            @Override
            public void execute() throws Throwable {
                ValidateAnnotationUtils.validate(entity);
            }

        };

        assertThrows(expected, actual);
    }

    @Test
    void entity_has_list_size_greater_0_return_nothing() {
        HasLengthEntity entity = new HasLengthEntity();
        entity.setList(new ArrayList<String>() {
            {
                add("a");
            }
        });

        Executable actual = new Executable() {

            @Override
            public void execute() throws Throwable {
                ValidateAnnotationUtils.validate(entity);
            }

        };

        assertDoesNotThrow(actual);
    }
}


class HasLengthEntity {
    @HasLength
    private List<String> list;

    public HasLengthEntity() {}

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
