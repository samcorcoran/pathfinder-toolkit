package com.lateensoft.pathfinder.toolkit.model.character.stats;

import java.util.*;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.google.common.collect.Lists;
import com.lateensoft.pathfinder.toolkit.BaseApplication;
import com.lateensoft.pathfinder.toolkit.R;
import org.jetbrains.annotations.Nullable;

public class SkillSet implements Parcelable {
	public static final int ACRO = 1;
    public static final int APPRAISE = 2;
    public static final int BLUFF = 3;
    public static final int CLIMB = 4;
    public static final int CRAFT = 5;
    public static final int DIPLOM = 6;
    public static final int DISABLE_DEV = 7;
    public static final int DISGUISE = 8;
    public static final int ESCAPE = 9;
    public static final int FLY = 10;
    public static final int HANDLE_ANIMAL = 11;
    public static final int HEAL = 12;
    public static final int INTIMIDATE = 13;
    public static final int KNOW_ARCANA = 14;
    public static final int KNOW_DUNGEON = 15;
    public static final int KNOW_ENG = 16;
    public static final int KNOW_GEO = 17;
    public static final int KNOW_HIST = 18;
    public static final int KNOW_LOCAL = 19;
    public static final int KNOW_NATURE = 20;
    public static final int KNOW_NOBILITY = 21;
    public static final int KNOW_PLANES = 22;
    public static final int KNOW_RELIGION = 23;
    public static final int LING = 24;
    public static final int PERCEPT = 25;
    public static final int PERFORM = 26;
    public static final int PROF = 27;
    public static final int RIDE = 28;
    public static final int SENSE_MOTIVE = 29;
    public static final int SLEIGHT_OF_HAND = 30;
    public static final int SPELLCRAFT = 31;
    public static final int STEALTH = 32;
    public static final int SURVIVAL = 33;
    public static final int SWIM = 34;
    public static final int USE_MAGIC_DEVICE = 35;
    
	List<Skill> m_skills;
	
	/**
	 * @return an unmodifiable list of the skill keys, in order.
	 */
	public static List<Integer> SKILL_KEYS() {
		Integer[] keys = { ACRO, APPRAISE, BLUFF, CLIMB, CRAFT, DIPLOM,
		    	DISABLE_DEV,DISGUISE,ESCAPE,FLY,HANDLE_ANIMAL, HEAL,INTIMIDATE, KNOW_ARCANA, KNOW_DUNGEON,
		    	KNOW_ENG, KNOW_GEO, KNOW_HIST, KNOW_LOCAL, KNOW_NATURE, KNOW_NOBILITY, KNOW_PLANES, KNOW_RELIGION,
		    	LING,  PERCEPT, PERFORM, PROF, RIDE, SENSE_MOTIVE, SLEIGHT_OF_HAND,SPELLCRAFT, STEALTH,
		    	SURVIVAL, SWIM, USE_MAGIC_DEVICE };
		return Collections.unmodifiableList(Arrays.asList(keys));
	}
	
	/**
	 * @return an unmodifiable list skill keys which can be subtyped.
	 */
	public static List<Integer> SUBTYPED_SKILLS() {
		Integer[] keys = {CRAFT, PERFORM, PROF};
		return Collections.unmodifiableList(Arrays.asList(keys));
	}

    public interface CorrectionListener {
        public void onInvalidSkillRemoved(Skill removedSkill);
        public void onMissingSkillAdded(Skill addedSkill);
        public void onSkillModified(Skill modifiedSkill);
    }

    public static SkillSet newValidatedSkillSet(List<Skill> skills, @Nullable CorrectionListener listener) {
        SkillSet skillSet = new SkillSet(skills);
        skillSet.validate(listener);
        return skillSet;
    }
	
	public SkillSet() {
		int[] defaultSkillAbilityIds = getDefaultAbilityIds();
		List<Integer> constSkillKeys = SKILL_KEYS();
		m_skills = Lists.newArrayListWithCapacity(constSkillKeys.size());
		
		for(int i = 0; i < constSkillKeys.size(); i++) {
			m_skills.add(new Skill(constSkillKeys.get(i), defaultSkillAbilityIds[i]));
		}
	}

	private SkillSet(List<Skill> skills) {
        m_skills = skills;
	}
	
	public SkillSet(Parcel in) {
        m_skills = Lists.newArrayList();
        in.readTypedList(m_skills, Skill.CREATOR);
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
        out.writeTypedList(m_skills);
	}

    /**
     * Ensures that all skills occur at least once.
     * If a skill does not appear, add a default version.
     * Also verifies that all skills have valid skill keys, and ability keys
     * Sorts skills once finished.
     */
    public void validate(@Nullable CorrectionListener listener) {
        SparseIntArray defaultAbilityKeys = getDefaultAbilityKeyMap();
        List<Skill> skillsToRemove = Lists.newArrayList();
        for (Skill skill : m_skills) {
            if (!isValidSkill(skill.getSkillKey())) {
                skillsToRemove.add(skill);
            } else if (!AbilitySet.isValidAbility(skill.getAbilityKey())) {
                skill.setAbilityKey(defaultAbilityKeys.get(skill.getSkillKey()));
                if (listener != null) {
                    listener.onSkillModified(skill);
                }
            }
        }
        m_skills.removeAll(skillsToRemove);
        if (listener != null) {
            for (Skill removedSkill : skillsToRemove) {
                listener.onInvalidSkillRemoved(removedSkill);
            }
        }

        boolean found;
        List<Integer> constSkillKeys = SKILL_KEYS();
        for (Integer skillKey : constSkillKeys) {
            found = false;
            for (Skill skill : m_skills) {
                if (skill.getSkillKey() == skillKey) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Skill newSkill = new Skill(skillKey, defaultAbilityKeys.get(skillKey));
                newSkill.setCharacterID(m_skills.get(0).getCharacterID());
                m_skills.add(newSkill);
                if (listener != null) {
                    listener.onMissingSkillAdded(newSkill);
                }
            }
        }

        Collections.sort(m_skills);
    }
	
	public Skill getSkillByIndex(int index) {
	    return m_skills.get(index);
	}
	
	public void setCharacterID(long id) {
		for (Skill skill : m_skills) {
			skill.setCharacterID(id);
		}
	}
	
	/**
	 * @return reference to skill at index. Index corresponds to array created by getTrainedSkills
	 */
	public Skill getTrainedSkill(int index){
		int trainedSkillIndex = 0;
        for (Skill m_skill : m_skills) {
            if (m_skill.getRank() > 0) {
                if (trainedSkillIndex == index) {
                    return m_skill;
                } else {
                    trainedSkillIndex++;
                }
            }
        }
		return null;
	}
	
	public List<Skill> getSkills(){
		return m_skills;
	}
	
	public int size() {
		return m_skills.size();
	}
	
	public ArrayList<Skill> getTrainedSkills(){
		ArrayList<Skill> trainedSkills = new ArrayList<Skill>();
        for (Skill m_skill : m_skills) {
            if (m_skill.getRank() > 0) {
                trainedSkills.add(m_skill);
            }
        }

        return trainedSkills;
	}
	
	public Skill addNewSubSkill(int skillKey) {
		int abilityKey = getDefaultAbilityKeyMap().get(skillKey);
		Skill newSkill = new Skill(skillKey, abilityKey);
		newSkill.setCharacterID(m_skills.get(0).getCharacterID());
		m_skills.add(newSkill);
		Collections.sort(m_skills);
		return newSkill;
	}
	
	public void deleteSkill(Skill skill) {
		m_skills.remove(skill);
	}

	public boolean hasMultipleOfSkill(int skillKey) {
		int numOfSkill = 0;
		for (Skill skill : m_skills) {
			if (skill.getSkillKey() == skillKey) {
				numOfSkill++;
			}
			if (numOfSkill > 1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return true if all subtypes of this skill are either trained or named
	 */
	public boolean allSubSkillsUsed(int skillKey) {
        for (Skill m_skill : m_skills) {
            if (m_skill.getSkillKey() == skillKey && m_skill.getRank() == 0
                    && (m_skill.getSubType() == null || m_skill.getSubType().isEmpty())) {
                return false;
            }
        }
		return true;
	}
	
	/**
	 * @return The default abilitykey for the skills, ordered as SKILL_KEYS
	 */
	private static int[] getDefaultAbilityIds() {
		Resources r = BaseApplication.getAppContext().getResources();
		return r.getIntArray(R.array.default_skill_ability_keys);
	}
	
	/**
	 * @return a map of the skill key to the ability key
	 */
	public static SparseIntArray getDefaultAbilityKeyMap() {
		List<Integer> constSkillKeys = SKILL_KEYS();
		SparseIntArray map = new SparseIntArray(constSkillKeys.size());
		int[] abilityIds = getDefaultAbilityIds();
		for(int i = 0; i < constSkillKeys.size(); i++) {
			map.append(constSkillKeys.get(i), abilityIds[i]);
		}
		return map;
	}
	
	/**
	 * @return the skill names, in the order as defined by SKILL_KEYS
	 */
	public static String[] getSkillNames() {
		Resources res = BaseApplication.getAppContext().getResources();
		return res.getStringArray(R.array.skills);
	}
	
	public static boolean isSubtypedSkill(int skillKey) {
		List<Integer> subtypedSkills = SUBTYPED_SKILLS();
		for (int key : subtypedSkills) {
			if (key == skillKey) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return a map of the skill keys to their name
	 */
	public static SparseArray<String> getSkillNameMap() {
		List<Integer> constSkillKeys = SKILL_KEYS();
		SparseArray<String> map = new SparseArray<String>(constSkillKeys.size());
		String[] names = getSkillNames();
		for (int i = 0; i < names.length; i++) {
			map.append(constSkillKeys.get(i), names[i]);
		}
		return map;
	}	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<SkillSet> CREATOR = new Parcelable.Creator<SkillSet>() {
		public SkillSet createFromParcel(Parcel in) {
			return new SkillSet(in);
		}
		
		public SkillSet[] newArray(int size) {
			return new SkillSet[size];
		}
	};

    public static boolean isValidSkill(int skillKey) {
        return (skillKey > 0 && skillKey <= SKILL_KEYS().size());
    }
}

