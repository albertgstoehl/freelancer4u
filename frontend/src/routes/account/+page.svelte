<script>
    import { user } from "../../store";

    // Get initials for the avatar
    $: initials = $user?.name
        ? $user.name
              .split(" ")
              .slice(0, 2)
              .map((n) => n[0].toUpperCase())
              .join("")
        : "";
</script>

<div class="container mt-4">
    <h1 class="text-danger mb-4">Account Details</h1>

    {#if $user}
        <div class="d-flex align-items-start mb-4">
            <div
                class="avatar me-4"
                style="width: 100px; height: 100px; background-color: #F8BBD0; color: white; display: flex; align-items: center; justify-content: center; font-size: 2.5rem; border-radius: 8px;"
            >
                {initials}
            </div>
        </div>

        <div class="flex-grow-1">
            <div class="mb-3">
                <label class="fw-bold">Name:</label>
                <div>{$user.name}</div>
            </div>

            <div class="mb-3">
                <label class="fw-bold">Nickname:</label>
                <div>{$user.nickname}</div>
            </div>

            <div class="mb-3">
                <label class="fw-bold">Email:</label>
                <div>{$user.email}</div>
            </div>

            {#if $user.given_name}
                <div class="mb-3">
                    <label class="fw-bold">First Name:</label>
                    <div>{$user.given_name}</div>
                </div>
            {/if}

            {#if $user.family_name}
                <div class="mb-3">
                    <label class="fw-bold">Last Name:</label>
                    <div>{$user.family_name}</div>
                </div>
            {/if}

            {#if $user.user_roles && $user.user_roles.length > 0}
                <div class="mb-3">
                    <label class="fw-bold">Roles:</label>
                    <div class="d-flex gap-2">
                        {#each $user.user_roles as role}
                            <span class="badge bg-primary">{role}</span>
                        {/each}
                    </div>
                </div>
            {/if}
        </div>
    {:else}
        <div class="alert alert-warning">
            Please log in to view your account details.
        </div>
    {/if}
</div>

<style>
    .avatar {
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    label {
        color: #666;
        font-size: 0.9rem;
        margin-bottom: 0.25rem;
    }
</style>
