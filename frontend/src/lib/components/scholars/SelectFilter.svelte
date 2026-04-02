<script lang="ts">
	interface Props {
		label: string;
		value: string;
		options: string[];
		onchange: (value: string) => void;
	}

	let { label, value, options, onchange }: Props = $props();
</script>

<div class="filter-group">
	<label for={label}>{label}</label>
	<select id={label} value={value} onchange={(e) => onchange(e.currentTarget.value)}>
		<option value="">Select {label}</option>
		{#each options as option}
			<option value={option}>{option}</option>
		{/each}
	</select>
</div>

<style>
	.filter-group {
		display: flex;
		flex-direction: column;
		gap: 0.5rem;
	}

	label {
		font-weight: 500;
		font-size: 0.875rem;
	}

	select {
		padding: 0.5rem;
		border: 1px solid #ccc;
		border-radius: 4px;
		font-size: 0.875rem;
		background-color: white;
		cursor: pointer;
	}

	select:hover {
		border-color: #999;
	}

	select:focus {
		outline: none;
		border-color: #333;
		box-shadow: 0 0 0 2px rgba(51, 51, 51, 0.1);
	}
</style>
