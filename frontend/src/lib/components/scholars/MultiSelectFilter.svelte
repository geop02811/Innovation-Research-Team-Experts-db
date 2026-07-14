<script lang="ts">
	interface Props {
		label: string;
		selected: string[];
		options: string[];
		onchange: (selected: string[]) => void;
		error?: string;
		onfocusout?: () => void;
		allowCustom?: boolean;
		customTrigger?: string;
		customPlaceholder?: string;
	}

	let {
		label,
		selected,
		options,
		onchange,
		error = '',
		onfocusout,
		allowCustom = false,
		customTrigger = 'Other',
		customPlaceholder
	}: Props = $props();
	let isOpen = $state(false);
	let customEntryOpen = $state(false);
	let customValue = $state('');
	let root: HTMLDivElement | undefined;

	const normalizeValue = (value: string) => value.trim().replace(/\s+/g, ' ');
	const valueKey = (value: string) => normalizeValue(value).toLowerCase();
	const isCustomTrigger = (option: string) => valueKey(option) === valueKey(customTrigger);
	const isSelected = (option: string) =>
		selected.some((item) => valueKey(item) === valueKey(option));
	const normalizedCustomValue = $derived(normalizeValue(customValue));
	const customInputPlaceholder = $derived(customPlaceholder ?? `Add custom ${label.toLowerCase()}`);

	const toggleOption = (option: string) => {
		if (allowCustom && isCustomTrigger(option)) {
			customEntryOpen = !customEntryOpen;
			isOpen = false;
			return;
		}

		const selectedKey = valueKey(option);
		const newSelected = isSelected(option)
			? selected.filter((item) => valueKey(item) !== selectedKey)
			: [...selected, option];
		onchange(newSelected);
	};

	const removeItem = (item: string) => {
		const selectedKey = valueKey(item);
		const newSelected = selected.filter((selectedItem) => valueKey(selectedItem) !== selectedKey);
		onchange(newSelected);
	};

	const addCustomValue = () => {
		if (!normalizedCustomValue || isCustomTrigger(normalizedCustomValue)) return;
		if (!isSelected(normalizedCustomValue)) {
			onchange([...selected, normalizedCustomValue]);
		}
		customValue = '';
		customEntryOpen = false;
	};

	const closeDropdown = () => {
		isOpen = false;
	};

	const handleFocusOut = (event: FocusEvent) => {
		if (event.relatedTarget instanceof Node && root?.contains(event.relatedTarget)) return;
		closeDropdown();
		onfocusout?.();
	};
</script>

<div class="multi-select-filter" bind:this={root} onfocusout={handleFocusOut}>
	<div class="label-container"><span class="label-text">{label}</span></div>
	<div class="dropdown-wrapper" role="presentation" onmouseleave={closeDropdown}>
		<button
			type="button"
			class="dropdown-button"
			onclick={() => (isOpen = !isOpen)}
			aria-expanded={isOpen}
			aria-haspopup="listbox"
			data-invalid={error ? 'true' : undefined}
		>
			<span class="selected-count">
				{selected.length > 0 ? `${selected.length} selected` : `${label}`}
			</span>
			<span class="arrow">▼</span>
		</button>

		{#if isOpen}
			<div class="dropdown-menu" role="listbox">
				{#each options as option}
					<label class="checkbox-item">
						<input
							type="checkbox"
							checked={allowCustom && isCustomTrigger(option)
								? customEntryOpen
								: isSelected(option)}
							onchange={() => toggleOption(option)}
						/>
						<span>{option}</span>
					</label>
				{/each}
			</div>
		{/if}
	</div>

	{#if allowCustom && customEntryOpen}
		<div class="custom-entry">
			<input
				class="custom-input"
				bind:value={customValue}
				placeholder={customInputPlaceholder}
				aria-label={customInputPlaceholder}
				onkeydown={(event) => {
					if (event.key === 'Enter') {
						event.preventDefault();
						addCustomValue();
					}
				}}
			/>
			<button
				type="button"
				class="custom-add"
				disabled={!normalizedCustomValue}
				onclick={addCustomValue}
			>
				Add
			</button>
		</div>
	{/if}

	{#if selected.length > 0}
		<div class="selected-items">
			{#each selected as item}
				<span class="badge">
					{item}
					<button type="button" class="remove" onclick={() => removeItem(item)}>×</button>
				</span>
			{/each}
		</div>
	{/if}

	{#if error}
		<p class="field-error" role="alert">{error}</p>
	{/if}
</div>

<style>
	.multi-select-filter {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
		min-width: 0;
	}

	.label-container {
		margin-bottom: 0.5rem;
	}

	.label-text {
		font-weight: 500;
		font-size: 0.875rem;
	}

	.dropdown-wrapper {
		position: relative;
		min-width: 0;
	}

	.dropdown-button {
		width: 100%;
		min-width: 0;
		padding: 0.5rem;
		border: 1px solid #ccc;
		border-radius: 4px;
		background-color: white;
		cursor: pointer;
		display: flex;
		justify-content: space-between;
		align-items: center;
		font-size: 0.875rem;
		transition: border-color 0.2s ease;
	}

	.dropdown-button:hover {
		border-color: #999;
	}

	.dropdown-button:focus {
		outline: none;
		border-color: #333;
		box-shadow: 0 0 0 2px rgba(51, 51, 51, 0.1);
	}

	.arrow {
		flex: 0 0 auto;
		font-size: 0.7rem;
		color: #666;
	}

	.selected-count {
		min-width: 0;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.dropdown-menu {
		position: absolute;
		top: 100%;
		left: 0;
		right: 0;
		background-color: white;
		border: 1px solid #ccc;
		border-top: none;
		border-radius: 0 0 4px 4px;
		box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
		max-height: 250px;
		overflow-y: auto;
		z-index: 10;
	}

	.checkbox-item {
		display: flex;
		align-items: center;
		gap: 0.5rem;
		padding: 0.5rem;
		cursor: pointer;
		font-size: 0.875rem;
		transition: background-color 0.2s ease;
	}

	.checkbox-item:hover {
		background-color: #f5f5f5;
	}

	.checkbox-item input[type='checkbox'] {
		cursor: pointer;
	}

	.custom-entry {
		display: grid;
		grid-template-columns: minmax(0, 1fr) auto;
		gap: 0.5rem;
		align-items: center;
		min-width: 0;
	}

	.custom-input {
		box-sizing: border-box;
		width: 100%;
		min-width: 0;
		border: 1px dashed #cfd4de;
		border-radius: 999px;
		background: #fff;
		padding: 0.55rem 1rem;
		font: inherit;
		font-size: 0.875rem;
		font-weight: 500;
		color: var(--ink);
	}

	.custom-input:focus {
		outline: none;
		border-color: #0a3a8d;
		box-shadow: 0 0 0 3px rgba(10, 58, 141, 0.12);
	}

	.custom-add {
		border: 0;
		border-radius: 999px;
		background: #0a3a8d;
		color: #fff;
		cursor: pointer;
		font: inherit;
		font-size: 0.875rem;
		font-weight: 700;
		padding: 0.58rem 1rem;
		white-space: nowrap;
	}

	.custom-add:disabled {
		cursor: not-allowed;
		opacity: 0.55;
	}

	.selected-items {
		display: flex;
		flex-wrap: wrap;
		gap: 0.5rem;
		min-width: 0;
	}

	.badge {
		display: inline-flex;
		align-items: center;
		gap: 0.5rem;
		max-width: 100%;
		min-width: 0;
		padding: 0.4rem 0.75rem;
		background-color: #f0f0f0;
		border: 1px solid #ddd;
		border-radius: 16px;
		font-size: 0.8rem;
		white-space: normal;
		overflow-wrap: anywhere;
	}

	.remove {
		background: none;
		border: none;
		color: #666;
		cursor: pointer;
		font-size: 1.1rem;
		padding: 0;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.remove:hover {
		color: #000;
	}

	.field-error {
		font-size: 0.84rem;
		font-weight: 600;
		color: #b42318;
		line-height: 1.35;
	}

	@media (max-width: 420px) {
		.custom-entry {
			grid-template-columns: 1fr;
		}

		.custom-add {
			width: 100%;
		}
	}
</style>
