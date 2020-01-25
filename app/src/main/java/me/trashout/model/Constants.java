/*
 * TrashOut is an environmental project that teaches people how to recycle 
 * and showcases the worst way of handling waste - illegal dumping. All you need is a smart phone.
 *  
 *  
 * There are 10 types of programmers - those who are helping TrashOut and those who are not.
 * Clean up our code, so we can clean up our planet. 
 * Get in touch with us: help@trashout.ngo
 *  
 * Copyright 2017 TrashOut, n.f.
 *  
 * This file is part of the TrashOut project.
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * See the GNU General Public License for more details: <https://www.gnu.org/licenses/>.
 */

package me.trashout.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import me.trashout.R;

public class Constants {

    public static final int MAX_PASSWORD_LENGTH = 50;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_EVENT_DISTANCE = 50000;
    public static String FEEDBACK_EMAIL_ADDRESS = "feedback@trashout.ngo";
    public static String PRIVACY_POLICY_HYPERLINK = "http://www.trashout.ngo/policy";
    public static String TERMS_CONDITIONS_HYPERLINK = "http://www.trashout.ngo/terms";
    public static final String CALENDAR_TYPE = "vnd.android.cursor.item/event";
    public static final String YOUTUBE_URL_PREFIX = "https://img.youtube.com/vi/";
    public static final String YOUTUBE_URL_SUFIX = "/default.jpg";
    public static final String EN_LOCALE = "en_US";

    public static String FAQ_HYPERLINK = "https://www.trashout.ngo/FAQ";
    public static String FAQ_HYPERLINK_SK = "https://www.trashout.ngo/sk/FAQ";
    public static String FAQ_HYPERLINK_FR = "https://www.trashout.ngo/fr/FAQ";
    public static String FAQ_HYPERLINK_ES = "https://www.trashout.ngo/es-ar/FAQ";
    public static String FAQ_HYPERLINK_RU = "https://www.trashout.ngo/ru/FAQ";
    public static String FAQ_HYPERLINK_DE = "https://www.trashout.ngo/de/FAQ";
    public static String FAQ_HYPERLINK_CS = "https://www.trashout.ngo/cs/FAQ";

    public static String SUPPORT_HYPERLINK = "https://www.trashout.ngo/projectsupport";
    public static String SUPPORT_HYPERLINK_SK = "https://www.trashout.ngo/sk/projectsupport";
    public static String SUPPORT_HYPERLINK_FR = "https://www.trashout.ngo/fr/projectsupport";
    public static String SUPPORT_HYPERLINK_ES = "https://www.trashout.ngo/es-ar/projectsupport";
    public static String SUPPORT_HYPERLINK_RU = "https://www.trashout.ngo/ru/projectsupport";
    public static String SUPPORT_HYPERLINK_DE = "https://www.trashout.ngo/de/projectsupport";
    public static String SUPPORT_HYPERLINK_CS = "https://www.trashout.ngo/cs/projectsupport";

    public static String ORDER_TRASH_PICKUP_HYPERLINK = "https://www.trashout.ngo/waste-management";
    public static String ORDER_TRASH_PICKUP_HYPERLINK_SK = "https://www.trashout.ngo/sk/waste-management";
    public static String ORDER_TRASH_PICKUP_HYPERLINK_RU = "https://www.trashout.ngo/ru/waste-management";
    public static String ORDER_TRASH_PICKUP_HYPERLINK_DE = "https://www.trashout.ngo/de/waste-management";
    public static String ORDER_TRASH_PICKUP_HYPERLINK_ES = "https://www.trashout.ngo/es-ar/waste-management";
    public static String ORDER_TRASH_PICKUP_HYPERLINK_CS = "https://www.trashout.ngo/cs/waste-management";
    public static String ORDER_TRASH_PICKUP_HYPERLINK_FR = "https://www.trashout.ngo/fr/waste-management";

    public static String ADD_RECYCLING_POINT = "https://admin.trashout.ngo/collection-points/create/ ";
    public static String EDIT_RECYCLING_POINT = "https://admin.trashout.ngo/collection-points/update/";
    public static String EDIT_EVENT = "https://admin.trashout.ngo/events/detail/";


    public enum TrashSize {
        BAG(0, "bag", R.string.trash_size_bag, R.drawable.ic_trash_size_bag),
        WHEELBARROW(1, "wheelbarrow", R.string.trash_size_wheelbarrow, R.drawable.ic_trash_size_wheelbarrow),
        CAR(2, "car", R.string.trash_size_carNeeded, R.drawable.ic_trash_size_car);

        private int id;
        private String name;
        private int stringResId;
        private int iconResId;

        TrashSize(int id, String name, int stringResId, int iconResId) {
            this.id = id;
            this.name = name;
            this.stringResId = stringResId;
            this.iconResId = iconResId;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getStringResId() {
            return stringResId;
        }

        public int getIconResId() {
            return iconResId;
        }

        public static TrashSize getTrashSizeByName(String name) {
            for (TrashSize trashSize : TrashSize.values()) {
                if (trashSize.getName().equalsIgnoreCase(name))
                    return trashSize;
            }
            return null;
        }
    }

    public enum TrashType {

        PLASTIC(0, "plastic", R.string.trash_types_plastic, R.drawable.btn_trash_type_plastic, R.color.background_trash_type_plastic),
        DOMESTIC(1, "domestic", R.string.trash_types_domestic, R.drawable.btn_trash_type_household, R.color.background_trash_type_household),
        AUTOMOTIVE(2, "automotive", R.string.trash_types_automotive, R.drawable.btn_trash_type_automotive, R.color.background_trash_type_automotive),
        LIQUID(3, "liquid", R.string.trash_types_liquid, R.drawable.btn_trash_type_liquid, R.color.background_trash_type_liquid),
        DANGEROUS(4, "dangerous", R.string.trash_types_dangerous, R.drawable.btn_trash_type_dangerous, R.color.background_trash_type_dangerous),
        METAL(5, "metal", R.string.trash_types_metal, R.drawable.btn_trash_type_metal, R.color.background_trash_type_metal),
        ELECTRICS(6, "electronic", R.string.trash_types_electronic, R.drawable.btn_trash_type_electronic, R.color.background_trash_type_electronic),
        DEAD_ANIMALS(7, "deadAnimals", R.string.trash_types_deadAnimals, R.drawable.btn_trash_type_dead_animals, R.color.background_trash_type_dead_animals),
        ORGANIC(8, "organic", R.string.trash_types_organic, R.drawable.btn_trash_type_organic, R.color.background_trash_type_organic),
        CONSTRUCTION(9, "construction", R.string.trash_types_construction, R.drawable.btn_trash_type_construction, R.color.background_trash_type_construction),
        GLASS(10, "glass", R.string.trash_types_glass, R.drawable.btn_trash_type_glass, R.color.background_trash_type_glass);

        private int id;
        private String name;
        private int stringResId;
        private int iconResId;
        private int bgColorResId;

        TrashType(int id, String name, int stringResId, int iconResId, int bgColorResId) {
            this.id = id;
            this.name = name;
            this.stringResId = stringResId;
            this.iconResId = iconResId;
            this.bgColorResId = bgColorResId;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getStringResId() {
            return stringResId;
        }

        public int getIconResId() {
            return iconResId;
        }

        public int getBgColorResId() {
            return bgColorResId;
        }

        public static TrashType getTrashTypeByName(String name) {
            for (TrashType trashType : TrashType.values()) {
                if (trashType.getName().equalsIgnoreCase(name))
                    return trashType;
            }
            return null;
        }

        public static ArrayList<String> getTrashTypeNameList(Context context, List<TrashType> trashTypeList) {
            ArrayList<String> trashTypeNameList = new ArrayList<>(trashTypeList.size());
            for (TrashType trashType : trashTypeList) {
                if (trashType != null) {
                    trashTypeNameList.add(context.getString(trashType.getStringResId()));
                }
            }

            return trashTypeNameList;
        }
    }

    public enum ActivityAction {
        CREATE(0, "create", R.string.notifications_reported, R.drawable.ic_trash_activity_reported),
        UPDATE(1, "update", R.string.trash_updated, R.drawable.ic_trash_activity_updated);

        private int id;
        private String name;
        private int stringUpdateActionResId;
        private int iconUpdateActionResId;

        ActivityAction(int id, String name, int stringUpdateActionResId, int iconUpdateActionResId) {
            this.id = id;
            this.name = name;
            this.stringUpdateActionResId = stringUpdateActionResId;
            this.iconUpdateActionResId = iconUpdateActionResId;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getStringUpdateActionResId() {
            return stringUpdateActionResId;
        }

        public int getIconUpdateActionResId() {
            return iconUpdateActionResId;
        }
    }

    public enum TrashStatus {
        STILL_HERE(0, "stillHere", R.string.trash_status_stillHere, R.drawable.ic_trash_status_remain, R.string.trash_updated, R.drawable.ic_trash_activity_updated),
        LESS(1, "less", R.string.trash_status_less, R.drawable.ic_trash_status_remain, R.string.trash_updated, R.drawable.ic_trash_activity_updated),
        MORE(2, "more", R.string.trash_status_more, R.drawable.ic_trash_status_remain, R.string.trash_updated, R.drawable.ic_trash_activity_updated),
        CLEANED(3, "cleaned", R.string.trash_status_cleaned, R.drawable.ic_trash_status_clean, R.string.trash_updated, R.drawable.ic_trash_activity_cleaned),
        UNKNOWN(4, "unknown", R.string.global_unknow, R.drawable.ic_trash_status_unknown, R.string.notifications_reported, R.drawable.ic_trash_activity_reported);

        private int id;
        private String name;
        private int stringResId;
        private int iconResId;
        private int stringUpdateHistoryResId;
        private int iconUpdatehistoryResId;

        TrashStatus(int id, String name, int stringResId, int iconResId, int stringUpdateHistoryResId, int iconUpdatehistoryResId) {
            this.id = id;
            this.name = name;
            this.stringResId = stringResId;
            this.iconResId = iconResId;
            this.stringUpdateHistoryResId = stringUpdateHistoryResId;
            this.iconUpdatehistoryResId = iconUpdatehistoryResId;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getStringResId() {
            return stringResId;
        }

        public int getIconResId() {
            return iconResId;
        }

        public int getStringUpdateHistoryResId() {
            return stringUpdateHistoryResId;
        }

        public int getIconUpdatehistoryResId() {
            return iconUpdatehistoryResId;
        }

        public static TrashStatus getTrashStatusByName(String name) {
            for (TrashStatus trashStatus : TrashStatus.values()) {
                if (trashStatus.getName().equalsIgnoreCase(name))
                    return trashStatus;
            }
            return UNKNOWN;
        }
    }


    public enum LastUpdate {
        NO_LIMIT, TODAY, LAST_WEEK, LAST_MONTH, LAST_YEAR
    }

    public enum CollectionPointSize {
        ALL(0, "all", R.string.collectionPoint_size_all),
        DUSTBIN(1, "dustbin", R.string.collectionPoint_size_recyclingBin),
        SCRAPYARD(2, "scrapyard", R.string.collectionPoint_size_recyclingCenter);

        private int id;
        private String name;
        private int stringResId;

        CollectionPointSize(int id, String name, int stringResId) {
            this.id = id;
            this.name = name;
            this.stringResId = stringResId;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getStringResId() {
            return stringResId;
        }

        public static CollectionPointSize getCollectionPointSizeByName(String name) {
            for (CollectionPointSize collectionPointSize : CollectionPointSize.values()) {
                if (collectionPointSize.getName().equalsIgnoreCase(name))
                    return collectionPointSize;
            }
            return null;
        }
    }

    // TODO check collection types value
    public enum CollectionPointType {
        PAPER(1, "paper", R.string.collectionPoint_types_paper, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        GLASS_ALL(2, "glassAll", R.string.collectionPoint_types_glassAll, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        GLASS_WHITE(3, "glassWhite", R.string.collectionPoint_types_glassWhite, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        GLASS_GOLD(4, "glassGold", R.string.collectionPoint_types_glassGold, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        GLASS_GREEN(5, "glassGreen", R.string.collectionPoint_types_glassGreen, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        METAL(6, "metal", R.string.collectionPoint_types_metal, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        PLASTIC(7, "plastic", R.string.collectionPoint_types_plastic, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        DANGEROUS(8, "dangerous", R.string.collectionPoint_types_dangerous, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        CARDBOARD(9, "cardboard", R.string.collectionPoint_types_cardboard, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        CLOTHES(10, "clothes", R.string.collectionPoint_types_clothes, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        BIODEGRADABLE(11, "biodegradable", R.string.collectionPoint_types_biodegradable, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        ELECTRONIC(12, "electronic", R.string.collectionPoint_types_electronic, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        EVERYTHING(13, "everything", R.string.collectionPoint_types_everything, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        RECYCLABLES(14, "recyclables", R.string.collectionPoint_types_recyclables, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.ALL),
        WIRED_GLASS(15, "wiredGlass", R.string.collectionPoint_types_wiredGlass, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        BATTERY(16, "battery", R.string.collectionPoint_types_battery, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        TIRES(17, "tires", R.string.collectionPoint_types_tires, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        IRON(18, "iron", R.string.collectionPoint_types_iron, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        WOODEN_AND_UPHOLSTERED_FURNITURE(19, "woodenAndUpholsteredFurniture", R.string.collectionPoint_types_woodenAndUpholsteredFurniture, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        CARPETS(20, "carpets", R.string.collectionPoint_types_carpets, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        WOODEN(21, "wooden", R.string.collectionPoint_types_wooden, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        WINDOW(22, "window", R.string.collectionPoint_types_window, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        BUILDING_RUBBLE(23, "buildingRubble", R.string.collectionPoint_types_buildingRubble, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        OIL(24, "oil", R.string.collectionPoint_types_oil, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        FLUORESCENT_LAMPS(25, "fluorescentLamps", R.string.collectionPoint_types_fluorescentLamps, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        NEON_LAMPS(26, "neonLamps", R.string.collectionPoint_types_neonLamps, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        LIGHT_BULBS(27, "lightBulbs", R.string.collectionPoint_types_lightBulbs, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        COLOR(28, "color", R.string.collectionPoint_types_color, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        THINNER(29, "thinner", R.string.collectionPoint_types_thinner, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        MIRROR(30, "mirror", R.string.collectionPoint_types_mirror, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        CAR_PARTS(31, "carParts", R.string.collectionPoint_types_carParts, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        MEDICINES(32, "medicines", R.string.collectionPoint_types_medicines, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        MATERIALS_FROM_BITUMINOUS_PAPER(33, "materialsFromBituminousPaper", R.string.collectionPoint_types_bitumenPaper, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        ETERNIT_COVERINGS(34, "eternitCoverings", R.string.collectionPoint_types_eternitCoverings, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        ASBESTOS(35, "asbestos", R.string.collectionPoint_types_asbestos, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        FIREPLACES(36, "fireplaces", R.string.collectionPoint_types_fireplaces, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        SLAG(37, "slag", R.string.collectionPoint_types_slag, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        GLASS_WOOL(38, "glassWool", R.string.collectionPoint_types_glassWool, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        CINDER(39, "cinder", R.string.collectionPoint_types_cinder, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        ASPHALT(40, "asphalt", R.string.collectionPoint_types_asphalt, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD),
        BITUMEN_PAPER(41, "bitumenPaper", R.string.collectionPoint_types_bitumenPaper, R.drawable.ic_dashboard_dustbin, R.color.background_collection_point_type, CollectionPointSize.SCRAPYARD);

        private int id;
        private String name;
        private int stringResId;
        private int iconResId;
        private int bgColorResId;
        private CollectionPointSize collectionPointSize;

        CollectionPointType(int id, String name, int stringResId, int iconResId, int bgColorResId, CollectionPointSize collectionPointSize) {
            this.id = id;
            this.name = name;
            this.stringResId = stringResId;
            this.iconResId = iconResId;
            this.bgColorResId = bgColorResId;
            this.collectionPointSize = collectionPointSize;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getStringResId() {
            return stringResId;
        }

        public int getIconResId() {
            return iconResId;
        }

        public int getBgColorResId() {
            return bgColorResId;
        }

        public CollectionPointSize getCollectionPointSize() {
            return collectionPointSize;
        }

        public static CollectionPointType getCollectionPointTypeByName(String name) {
            for (CollectionPointType collectionPointType : CollectionPointType.values()) {
                if (collectionPointType.getName().equalsIgnoreCase(name))
                    return collectionPointType;
            }
            return null;
        }

        public static ArrayList<String> getCollectionPointTypeNameList(Context context, List<CollectionPointType> collectionPointTypes) {
            ArrayList<String> collectionPointTypeNameList = new ArrayList<>(collectionPointTypes.size());
            for (CollectionPointType collectionPointType : collectionPointTypes)
                collectionPointTypeNameList.add(context.getString(collectionPointType.getStringResId()));

            return collectionPointTypeNameList;
        }
    }


    public enum UserActivityType {
        TRASH_POINT(0, "trashPoint", R.string.collectionPoint_activity),
        EVENT(1, "event", R.string.event_activity);

        private int id;
        private String name;
        private int stringResId;

        UserActivityType(int id, String name, int stringResId) {
            this.id = id;
            this.name = name;
            this.stringResId = stringResId;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getStringResId() {
            return stringResId;
        }

        public static UserActivityType getUserActivityTypeByName(String name) {
            for (UserActivityType userActivityType : UserActivityType.values()) {
                if (userActivityType.getName().equalsIgnoreCase(name))
                    return userActivityType;
            }
            return null;
        }
    }
}
