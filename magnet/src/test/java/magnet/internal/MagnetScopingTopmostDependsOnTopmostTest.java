package magnet.internal;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import magnet.Scope;
import magnet.Scoping;

public class MagnetScopingTopmostDependsOnTopmostTest {

    private MagnetScope scope1;
    private MagnetScope scope2;
    private MagnetScope scope3;

    @Before
    public void before() {
        InstanceManager instanceManager = new StubInstanceManager();

        scope1 = (MagnetScope) new MagnetScope(null, instanceManager)
                .bind(Dependency1.class, new Dependency1());

        scope2 = (MagnetScope) scope1.createSubscope()
                .bind(Dependency2.class, new Dependency2());

        scope3 = (MagnetScope) scope2.createSubscope()
                .bind(Dependency3.class, new Dependency3());
    }

    private interface MenuItem {}

    private static class MenuItemZero implements MenuItem {}
    private static class MenuItemOne implements MenuItem {}
    private static class MenuItemTwo implements MenuItem {}
    private static class MenuItemThree implements MenuItem {}

    private static class Dependency1 {}
    private static class Dependency2 {}
    private static class Dependency3 {}

    private static class MenuItemZeroFactory implements InstanceFactory<MenuItem> {
        @Override public MenuItem create(Scope scope) {
            return new MenuItemZero();
        }
        @Override public Scoping getScoping() { return Scoping.TOPMOST; }
    }

    private static class MenuItemOneFactory implements InstanceFactory<MenuItem> {
        @Override public MenuItem create(Scope scope) {
            scope.getSingle(Dependency1.class);
            return new MenuItemOne();
        }
        @Override public Scoping getScoping() { return Scoping.TOPMOST; }
    }

    private static class MenuItemTwoFactory implements InstanceFactory<MenuItem> {
        @Override public MenuItem create(Scope scope) {
            scope.getSingle(Dependency2.class);
            scope.getSingle(MenuItem.class, "one");
            return new MenuItemTwo();
        }
        @Override public Scoping getScoping() { return Scoping.TOPMOST; }
    }

    private static class MenuItemThreeFactory implements InstanceFactory<MenuItem> {
        @Override public MenuItem create(Scope scope) {
            scope.getSingle(Dependency3.class);
            scope.getSingle(MenuItem.class, "two");
            return new MenuItemThree();
        }
        @Override public Scoping getScoping() { return Scoping.TOPMOST; }
    }

    @Test
    public void itemZero_getSingleInScope3() {
        // when
        MenuItem item = scope3.getSingle(MenuItem.class, "zero");

        // then
        assertThat(item).isNotNull();
        assertThat(scope3.getRegisteredSingle(MenuItem.class, "zero")).isNull();
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "zero")).isNull();
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "zero")).isEqualTo(item);
    }

    @Test
    public void itemZero_getSingleInScope2() {
        // when
        MenuItem item = scope2.getSingle(MenuItem.class, "zero");

        // then
        assertThat(item).isNotNull();
        assertThat(scope3.getRegisteredSingle(MenuItem.class, "zero")).isNull();
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "zero")).isNull();
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "zero")).isEqualTo(item);
    }

    @Test
    public void itemZero_getSingleInScope1() {
        // when
        MenuItem item = scope1.getSingle(MenuItem.class, "zero");

        // then
        assertThat(item).isNotNull();
        assertThat(scope3.getRegisteredSingle(MenuItem.class, "zero")).isNull();
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "zero")).isNull();
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "zero")).isEqualTo(item);
    }

    @Test
    public void itemOne_getSingleInScope3() {
        // when
        MenuItem item = scope3.getSingle(MenuItem.class, "one");

        // then
        assertThat(item).isNotNull();
        assertThat(scope3.getRegisteredSingle(MenuItem.class, "one")).isNull();
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "one")).isNull();
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "one")).isEqualTo(item);
    }

    @Test
    public void itemOne_getSingleInScope2() {
        // when
        MenuItem item = scope2.getSingle(MenuItem.class, "one");

        // then
        assertThat(item).isNotNull();
        assertThat(scope3.getRegisteredSingle(MenuItem.class, "one")).isNull();
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "one")).isNull();
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "one")).isEqualTo(item);
    }

    @Test
    public void itemOne_getSingleInScope1() {
        // when
        MenuItem item = scope1.getSingle(MenuItem.class, "one");

        // then
        assertThat(item).isNotNull();
        assertThat(scope3.getRegisteredSingle(MenuItem.class, "one")).isNull();
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "one")).isNull();
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "one")).isEqualTo(item);
    }

    @Test
    public void itemTwo_getSingleInScope3() {
        // when
        MenuItem item = scope3.getSingle(MenuItem.class, "two");

        // then
        assertThat(item).isNotNull();
        assertThat(scope3.getRegisteredSingle(MenuItem.class, "two")).isNull();
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "two")).isEqualTo(item);
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "two")).isNull();

        assertThat(scope3.getRegisteredSingle(MenuItem.class, "one")).isNull();
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "one")).isNull();
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "one")).isNotNull();
    }

    @Test
    public void itemTwo_getSingleInScope2() {
        // when
        MenuItem item = scope2.getSingle(MenuItem.class, "two");

        // then
        assertThat(item).isNotNull();
        assertThat(scope3.getRegisteredSingle(MenuItem.class, "two")).isNull();
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "two")).isEqualTo(item);
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "two")).isNull();

        assertThat(scope3.getRegisteredSingle(MenuItem.class, "one")).isNull();
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "one")).isNull();
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "one")).isNotNull();
    }

    @Test(expected = IllegalStateException.class)
    public void itemTwo_getSingleInScope1() {
        // when
        scope1.getSingle(MenuItem.class, "two");
    }

    @Test
    public void itemThree_getSingleInScope3() {
        // when
        MenuItem item = scope3.getSingle(MenuItem.class, "three");

        // then
        assertThat(item).isNotNull();
        assertThat(scope3.getRegisteredSingle(MenuItem.class, "three")).isEqualTo(item);
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "three")).isNull();
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "three")).isNull();

        assertThat(scope3.getRegisteredSingle(MenuItem.class, "two")).isNull();
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "two")).isNotNull();
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "two")).isNull();

        assertThat(scope3.getRegisteredSingle(MenuItem.class, "one")).isNull();
        assertThat(scope2.getRegisteredSingle(MenuItem.class, "one")).isNull();
        assertThat(scope1.getRegisteredSingle(MenuItem.class, "one")).isNotNull();
    }

    @Test(expected = IllegalStateException.class)
    public void itemThree_getSingleInScope2() {
        // when
        scope2.getSingle(MenuItem.class, "three");
    }

    @Test(expected = IllegalStateException.class)
    public void itemThree_getSingleInScope1() {
        // when
        scope1.getSingle(MenuItem.class, "three");
    }

    private static class StubInstanceManager implements InstanceManager {
        private final Map<String, InstanceFactory<MenuItem>> factories;

        StubInstanceManager() {
            factories = new HashMap<>();
            factories.put("zero", new MenuItemZeroFactory());
            factories.put("one", new MenuItemOneFactory());
            factories.put("two", new MenuItemTwoFactory());
            factories.put("three", new MenuItemThreeFactory());
        }

        @Override public <T> InstanceFactory<T> getOptionalFactory(Class<T> type, String classifier) {
            //noinspection unchecked
            return (InstanceFactory<T>) factories.get(classifier);
        }
        @Override public <T> List<InstanceFactory<T>> getManyFactories(Class<T> type, String classifier) {
            throw new UnsupportedOperationException();
        }
    }


}
