<script lang="ts">
	interface Props {
		label: string;
		selected: string[];
		options: string[];
		onchange: (selected: string[]) => void;
	}

	let { label, selected, options, onchange }: Props = $props();
	let isOpen = $state(false);

	const toggleOption = (option: string) => {
		const newSelected = selected.includes(option)
			? selected.filter((item) => item !== option)
			: [...selected, option];
		onchange(newSelected);
	};

	const removeItem = (item: string) => {
		const newSelected = selected.filter((s) => s !== item);
		onchange(newSelected);
	};

	const closeDropdown = () => {
		isOpen = false;
	};
</script>

<div class="multi-select-filter">
	<div class="label-container"><span class="label-text">{label}</span></div>
	<div class="dropdown-wrapper" onmouseleave={closeDropdown}>
		<button
			type="button"
			class="dropdown-button"
			onclick={() => (isOpen = !isOpen)}
			aria-expanded={isOpen}
			aria-haspopup="listbox"
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
							checked={selected.includes(option)}
							onchange={() => toggleOption(option)}
						/>
						<span>{option}</span>
					</label>
				{/each}
			</div>
		{/if}
	</div>

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

	.selected-items {
		display: flex;
		flex-wrap: wrap;
		gap: 0.5rem;
	}

	.badge {
		display: inline-flex;
		align-items: center;
		gap: 0.5rem;
		padding: 0.4rem 0.75rem;
		background-color: #f0f0f0;
		border: 1px solid #ddd;
		border-radius: 16px;
		font-size: 0.8rem;
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
</style>
