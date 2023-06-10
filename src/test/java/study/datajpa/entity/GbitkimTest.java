package study.datajpa.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class GbitkimTest {

    @Test
    public void test(){
        Parent parent = new Parent();
        Child test = new Child("test", parent);
        test.addParent(parent);
    }

    @Data
    class Parent{
        private String name;
        List<Child> childs = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    class Child{
        private String name;
        private Parent parent;

        void addParent(Parent parent) {
            this.parent = parent;


            List<Child> childs = parent.childs;
            for (Child child : childs) {
                System.out.println(child);
            }
            childs.add(this);
        }
    }
}
