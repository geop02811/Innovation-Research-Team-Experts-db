<script lang="ts">
	import MultiSelectFilter from '$lib/components/scholars/MultiSelectFilter.svelte';
	import type { LanguageProficiency } from '$lib/auth/types';

	interface Props {
		label: string;
		selected: string[];
		entries: LanguageProficiency[];
		options: string[];
		proficiencyOptions: readonly string[];
		onLanguagesChange: (selected: string[]) => void;
		onEntriesChange: (entries: LanguageProficiency[]) => void;
		validationRequested?: boolean;
		allowCustom?: boolean;
		customPlaceholder?: string;
	}

	let {
		label,
		selected,
		entries,
		options,
		proficiencyOptions,
		onLanguagesChange,
		onEntriesChange,
		validationRequested = false,
		allowCustom = false,
		customPlaceholder
	}: Props = $props();
	let selectedTouched = $state(false);
	let proficiencyTouched = $state<Record<string, boolean>>({});

	const normalizedKey = (value: string) => value.trim().toLowerCase();
	const shouldShowSelectedError = () => validationRequested || selectedTouched;

	const getEntry = (language: string): LanguageProficiency => {
		const key = normalizedKey(language);
		return entries.find((entry) => normalizedKey(entry.language) === key) ?? { language, proficiency: '' };
	};

	const getSelectedError = () =>
		shouldShowSelectedError() && selected.length === 0 ? 'Please add at least one language.' : '';

	const touchProficiency = (language: string) => {
		proficiencyTouched[normalizedKey(language)] = true;
	};

	const getProficiencyError = (language: string) => {
		const entry = getEntry(language);
		if (!(validationRequested || proficiencyTouched[normalizedKey(language)])) return '';
		return entry.proficiency ? '' : 'Please select a proficiency level.';
	};

	const updateProficiency = (language: string, proficiency: string) => {
		onEntriesChange(
			selected.map((selectedLanguage) => {
				const entry = getEntry(selectedLanguage);
				return selectedLanguage === language ? { language: selectedLanguage, proficiency } : entry;
			})
		);
	};
</script>

<div class="language-proficiency-field">
	<MultiSelectFilter
		{label}
		{selected}
		{options}
		{allowCustom}
		{customPlaceholder}
		error={getSelectedError()}
		onfocusout={() => (selectedTouched = true)}
		onchange={onLanguagesChange}
	/>

	{#if selected.length > 0}
		<div class="proficiency-list" aria-label="Language proficiency levels">
			{#each selected as language}
				{@const entry = getEntry(language)}
				<label class="proficiency-row">
					<span class="language-name">{language}</span>
					<select
						value={entry.proficiency}
						aria-label={`${language} proficiency`}
						aria-invalid={getProficiencyError(language) ? 'true' : undefined}
						onfocusout={() => touchProficiency(language)}
						onchange={(event) => updateProficiency(language, event.currentTarget.value)}
					>
						<option value="">Select proficiency</option>
						{#each proficiencyOptions as option}
							<option value={option}>{option}</option>
						{/each}
					</select>
					{#if getProficiencyError(language)}
						<span class="field-error" role="alert">{getProficiencyError(language)}</span>
					{/if}
				</label>
			{/each}
		</div>
	{/if}
</div>

<style>
	.language-proficiency-field {
		display: grid;
		gap: 0.75rem;
		min-width: 0;
	}

	.proficiency-list {
		display: grid;
		gap: 0.55rem;
		min-width: 0;
	}

	.proficiency-row {
		display: grid;
		grid-template-columns: minmax(8rem, 1fr) minmax(10rem, 1.2fr);
		gap: 0.65rem;
		align-items: center;
		min-width: 0;
	}

	.language-name {
		min-width: 0;
		font-size: 0.9rem;
		font-weight: 700;
		color: var(--ink);
		overflow-wrap: anywhere;
	}

	select {
		box-sizing: border-box;
		width: 100%;
		min-width: 0;
		border: 1px solid #cfd4de;
		border-radius: 10px;
		background: #fff;
		padding: 0.58rem 0.7rem;
		font: inherit;
		font-size: 0.9rem;
		font-weight: 600;
		color: var(--ink);
	}

	select:focus {
		outline: none;
		border-color: #0a3a8d;
		box-shadow: 0 0 0 3px rgba(10, 58, 141, 0.12);
	}

	.field-error {
		font-size: 0.82rem;
		font-weight: 600;
		color: #b42318;
		line-height: 1.35;
	}

	@media (max-width: 620px) {
		.proficiency-row {
			grid-template-columns: 1fr;
			gap: 0.35rem;
		}
	}
</style>