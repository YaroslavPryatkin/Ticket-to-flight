package com.game.Ticket_To_Flight.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.game.Ticket_To_Flight.commonFrontAndBack.DTOHandler;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class Network {
    public static final int TCP_PORT = 54555;
    public static final int UDP_PORT = 54777;
    public static final int timeoutTime = 5000;

    //old register
    /*
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(GameStateRequest.class);
        kryo.register(GameStateResponse.class);
        kryo.register(GameStateResponse.State.class);
        kryo.register(PlayerStateRequest.class);
        kryo.register(PlayerStateResponse.class);
        kryo.register(PlayerState.class);
        kryo.register(PlaneType.class);
        kryo.register(Airline.class);
        kryo.register(Airport.class);
        kryo.register(Vector2.class);
        kryo.register(AirlineType.class);
        kryo.register(AirportType.class);
        kryo.register(ClosedRange.class);
        kryo.register(JoinGameRequest.class);
        kryo.register(JoinGameResponse.class);
        kryo.register(JoinGameResponse.Response.class);
    }
    */

    //root classes to register. Also, should be classes in <> because of type erase
    private static final Class[] ROOT_CLASSES = {

        JoinGameRequest.class,
        JoinGameResponse.class,
        Arrays.asList().getClass(),
        Collections.emptyList().getClass(),
        Collections.emptyMap().getClass(),
        Collections.emptySet().getClass(),
        Collections.singletonList(null).getClass(),
        ArrayList.class,
        HashMap.class,
        HashSet.class,
        TreeMap.class
    };

    // NOT to register
    private static final Set<Class> BLACKLIST = new HashSet<>(Arrays.asList(
        String.class, Integer.class, Double.class, Long.class, Boolean.class,
        int.class, long.class, double.class, float.class, boolean.class,
        void.class, Void.class,
        Object.class, Class.class, Enum.class,
        List.class, Map.class, Set.class, Collection.class, Iterable.class
    ));

    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        kryo.setRegistrationRequired(true);

        Set<Class> discovered = new TreeSet<>(Comparator.comparing(Class::getName));

        for (Class startClass : ROOT_CLASSES) {
            discoverRecursive(startClass, discovered);
        }

        for (Class clazz : discovered) {
            kryo.register(clazz);
            System.out.println(clazz.toString() + " registered for " + endPoint.toString());
        }
    }

    private static void discoverRecursive(Class<?> clazz, Set<Class> found) {
        if (clazz == null || clazz.isPrimitive() || BLACKLIST.contains(clazz) || found.contains(clazz)) {
            return;
        }

        if (clazz.isEnum() || clazz.getName().startsWith("java.") || clazz.getName().startsWith("javax.")) {
            return;
        }

        if (clazz.isArray()) {
            discoverRecursive(clazz.getComponentType(), found);
            return;
        }

        found.add(clazz);

        for (Field field : clazz.getDeclaredFields()) {
            discoverRecursive(field.getType(), found);

            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;

                for (Type typeArgument : pt.getActualTypeArguments()) {
                    if (typeArgument instanceof Class) {
                        discoverRecursive((Class<?>) typeArgument, found);
                    } else if (typeArgument instanceof ParameterizedType) {
                        discoverRecursive((Class<?>) ((ParameterizedType) typeArgument).getRawType(), found);
                    }
                }
            }
        }
    }

    public static class PlayerAirlineChoiceResponse{
        public Set<Integer> airlines;
    }

    public static class PlayerPlaneChoiceResponse{
        public Set<Integer> planes;
    }

    public static class PlayerInvestmentChoiceResponse{
        public Integer shares;
    }

    public static class PlayerAbilityChoiceResponse{
        public Integer ability;
    }

    public static class PlayerWorldEventChoiceResponse{
        public Integer worldEvent;
    }

    public static class PlayerRouteChoiceResponse{
        public Integer worldEvent;
    }

    public static class DataChangesMessage{
        //public DTOHandler.DataChangesDTO dcDTO;
    }

    public static class ReloadGameDataRequest{

    }

    public static class ReloadGameDataResponse{
        //public DTOHandler.DataChangesDTO dcDTO;
    }

    public static class JoinGameRequest {
        public String playerName;
    }

    public static class JoinGameResponse {
        public enum Response {
            SUCCESS,
            NAME_ALREADY_EXISTS,
            BADNAME
        }
        public Response response;
    }
}
