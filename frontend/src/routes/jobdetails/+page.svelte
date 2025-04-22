<!-- frontend/src/routes/jobdetails/+page.svelte -->
<script>
    import axios from "axios";
    import { onMount } from "svelte";
    import { page } from "$app/stores"; // Use $page store for query parameters
    import { jwt_token } from "../../store"; // Assuming store path is correct

    const api_root = $page.url.origin; // Get API root from page context

    let job = $state(null);
    let loading = $state(true);
    let error = $state(null);
    let jobId = $state(null);

    onMount(async () => {
        jobId = $page.url.searchParams.get('id'); // Get job ID from query parameter

        if (!jobId) {
            error = "Job ID is missing from the URL.";
            loading = false;
            return;
        }

        var config = {
            method: "get",
            url: `${api_root}/api/job/${jobId}`,
            headers: {
                "Authorization": "Bearer " + $jwt_token
            },
        };

        try {
            const response = await axios(config);
            job = response.data;
        } catch (err) {
            console.error("Error fetching job details:", err);
            if (err.response && err.response.status === 404) {
                error = "Job not found.";
            } else {
                error = "Could not fetch job details.";
            }
        } finally {
            loading = false;
        }
    });
</script>

<div class="container mt-4">
    {#if loading}
        <p>Loading job details...</p>
    {:else if error}
        <div class="alert alert-danger" role="alert">
            {error} <a href="/jobs" class="alert-link">Go back to jobs list</a>.
        </div>
    {:else if job}
        <h1>Job Details: {job.title || 'N/A'}</h1>
        <hr />
        <dl class="row">
            <dt class="col-sm-3">ID</dt>
            <dd class="col-sm-9">{job.id}</dd>

            <dt class="col-sm-3">Description</dt>
            <dd class="col-sm-9">{job.description || 'No description provided.'}</dd>

            <dt class="col-sm-3">Job Type</dt>
            <dd class="col-sm-9">{job.jobType}</dd>

            <dt class="col-sm-3">Earnings</dt>
            <dd class="col-sm-9">{job.earnings !== null ? job.earnings : 'N/A'}</dd>

            <dt class="col-sm-3">State</dt>
            <dd class="col-sm-9">{job.jobState}</dd>

            <dt class="col-sm-3">Company ID</dt>
            <dd class="col-sm-9">{job.companyId || 'N/A'}</dd>

            {#if job.freelancerId}
                <dt class="col-sm-3">Assigned Freelancer</dt>
                <dd class="col-sm-9">{job.freelancerId}</dd>
            {/if}
        </dl>
         <a href="/jobs" class="btn btn-secondary">Back to Jobs List</a>
    {:else}
         <p>No job data available.</p>
          <a href="/jobs" class="btn btn-secondary">Back to Jobs List</a>
    {/if}
</div>

<style>
    /* Optional: Add custom styles if needed */
    .container {
        max-width: 800px;
    }
    dt {
        font-weight: bold;
    }
</style> 