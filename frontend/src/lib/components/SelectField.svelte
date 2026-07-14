<script lang="ts">
	import { onMount } from 'svelte';

	interface Props {
		label: string;
		value: string;
		placeholder: string;
		options: readonly string[];
		onchange: (value: string) => void;
		error?: string;
		onfocusout?: () => void;
	}

	let { label, value, placeholder, options, onchange, error = '', onfocusout }: Props = $props();
	let isOpen = $state(false);
	let activeIndex = $state(0);
	let root: HTMLDivElement;

	const fieldId = $derived(`select-${label.toLowerCase().replace(/[^a-z0-9]+/g, '-')}`);
	const errorId = $derived(`${fieldId}-error`);
	const getOptionValues = () => ['', ...options];

	const openMenu = () => {
		const selectedIndex = getOptionValues().findIndex((option) => option === value);
		activeIndex = selectedIndex >= 0 ? selectedIndex : 0;
		isOpen = true;
	};

	const closeMenu = () => {
		isOpen = false;
	};

	const selectValue = (selectedValue: string) => {
		onchange(selectedValue);
		closeMenu();
	};

	const handleFocusOut = (event: FocusEvent) => {
		if (event.relatedTarget instanceof Node && root?.contains(event.relatedTarget)) return;
		closeMenu();
		onfocusout?.();
	};

	const moveActiveOption = (offset: number) => {
		const optionsCount = getOptionValues().length;
		activeIndex = (activeIndex + offset + optionsCount) % optionsCount;
	};

	const handleKeydown = (event: KeyboardEvent) => {
		if (!isOpen && ['ArrowDown', 'ArrowUp', 'Enter', ' '].includes(event.key)) {
			event.preventDefault();
			openMenu();
			return;
		}

		if (!isOpen) return;

		if (event.key === 'ArrowDown') {
			event.preventDefault();
			moveActiveOption(1);
		} else if (event.key === 'ArrowUp') {
			event.preventDefault();
			moveActiveOption(-1);
		} else if (event.key === 'Enter' || event.key === ' ') {
			event.preventDefault();
			selectValue(getOptionValues()[activeIndex] ?? '');
		} else if (event.key === 'Escape') {
			event.preventDefault();
			closeMenu();
		}
	};

	onMount(() => {
		const handlePointerDown = (event: PointerEvent) => {
			if (!root?.contains(event.target as Node)) closeMenu();
		};

		document.addEventListener('pointerdown', handlePointerDown);
		return () => document.removeEventListener('pointerdown', handlePointerDown);
	});
</script>

<div class="select-field" bind:this={root} onfocusout={handleFocusOut}>
	<span id={`${fieldId}-label`} class="select-label">{label}</span>
	<button
		type="button"
		class="select-button"
		class:placeholder={!value}
		onclick={() => (isOpen ? closeMenu() : openMenu())}
		onkeydown={handleKeydown}
		aria-haspopup="listbox"
		aria-expanded={isOpen}
		aria-labelledby={`${fieldId}-label ${fieldId}-value`}
		data-invalid={error ? 'true' : undefined}
		aria-describedby={error ? errorId : undefined}
	>
		<span id={`${fieldId}-value`} class="select-value">{value || placeholder}</span>
		<span class="select-arrow" aria-hidden="true">⌄</span>
	</button>

	{#if isOpen}
		<div class="select-menu" role="listbox" aria-labelledby={`${fieldId}-label`}>
			{#each getOptionValues() as option, index}
				<button
					type="button"
					class="select-option"
					class:active={activeIndex === index}
					class:selected={value === option}
					role="option"
					aria-selected={value === option}
					onmouseenter={() => (activeIndex = index)}
					onclick={() => selectValue(option)}
				>
					{option || placeholder}
				</button>
			{/each}
		</div>
	{/if}

	{#if error}
		<span id={errorId} class="field-error" role="alert">{error}</span>
	{/if}
</div>

<style>
	.select-field {
		position: relative;
		display: grid;
		gap: 0.45rem;
		min-width: 0;
		font-weight: 600;
		font-size: 0.95rem;
	}

	.select-button {
		box-sizing: border-box;
		width: 100%;
		max-width: 100%;
		min-width: 0;
		border-radius: 12px;
		border: 1px solid #cfd4de;
		padding: 0.66rem 0.8rem;
		font-size: 1rem;
		background: #fff;
		font-family: inherit;
		font-weight: 600;
		color: var(--ink);
		cursor: pointer;
		display: flex;
		align-items: center;
		justify-content: space-between;
		gap: 0.75rem;
		text-align: left;
	}

	.select-button.placeholder {
		color: #6f7788;
	}

	.select-button:focus,
	.select-button:focus-visible {
		outline: none;
		border-color: #0a3a8d;
		box-shadow: 0 0 0 3px rgba(10, 58, 141, 0.12);
	}

	.select-value {
		min-width: 0;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.select-arrow {
		flex: 0 0 auto;
		font-size: 0.9rem;
		line-height: 1;
		color: #6f7788;
	}

	.select-menu {
		position: absolute;
		top: calc(100% + 0.35rem);
		left: 0;
		right: 0;
		z-index: 20;
		max-height: min(16rem, 45vh);
		overflow-y: auto;
		border: 1px solid #cfd4de;
		border-radius: 12px;
		background: #fff;
		box-shadow: 0 16px 36px rgba(27, 43, 78, 0.18);
		padding: 0.3rem;
	}

	.select-option {
		width: 100%;
		border: 0;
		border-radius: 9px;
		background: transparent;
		padding: 0.62rem 0.7rem;
		font: inherit;
		font-size: 0.96rem;
		font-weight: 600;
		color: var(--ink);
		text-align: left;
		cursor: pointer;
		overflow-wrap: anywhere;
	}

	.select-option.active,
	.select-option:hover {
		background: #eef3fb;
		color: #0a3a8d;
	}

	.select-option.selected {
		background: #0a3a8d;
		color: #fff;
	}

	.field-error {
		font-size: 0.84rem;
		font-weight: 600;
		color: #b42318;
		line-height: 1.35;
	}

	.select-button[data-invalid='true'] {
		border-color: #b42318;
		box-shadow: 0 0 0 3px rgba(180, 35, 24, 0.12);
	}

	@media (max-width: 700px) {
		.select-menu {
			max-height: min(14rem, 42vh);
		}
	}
</style>
