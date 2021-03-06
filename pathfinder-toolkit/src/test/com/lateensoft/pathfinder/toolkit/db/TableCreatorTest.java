package com.lateensoft.pathfinder.toolkit.db;

import android.database.Cursor;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Config(manifest=Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class TableCreatorTest extends BaseDatabaseTest {

    @Test
    public void testDatabaseCreation() {
        Cursor c = getDatabase().query("sqlite_master", new String[]{"name"},
                "type='table' ORDER BY name ASC");

        List<String> tablesInDb = Lists.newArrayList();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            tablesInDb.add(c.getString(0));
            c.moveToNext();
        }

        tablesInDb.remove("android_metadata");
        tablesInDb.remove("sqlite_sequence");

        assertThat(tablesInDb, containsInAnyOrder("Ability", "Armor", "Character", "CombatStatSet",
                "Feat", "FluffInfo", "Item", "Party", "Save", "Skill",
                "Spell", "Weapon", "Encounter", "EncounterParticipant", "PartyMembership"));
    }
}
