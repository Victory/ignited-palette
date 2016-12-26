package org.dfhu.ignitedpalette.example;


@Singleton
public class SomeSingleton {
    private static final SomeSingleton INSTANCE = new SomeSingleton();

    private SomeSingleton() {}

    public static SomeSingleton getInstance() {
        return INSTANCE;
    }
}
