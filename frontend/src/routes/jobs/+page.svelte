<script>
  import axios from "axios";
  import { page } from "$app/stores";
  import { onMount } from "svelte";
  import { jwt_token, isAuthenticated, user } from "../../store";
  // get the origin of current page, e.g. http://localhost:8080
  const api_root = $page.url.origin;

  let currentPage = $state(1);
  let nrOfPages = $state(0);
  let defaultPageSize = $state(20);

  let earningsMinFilter = $state(null);
  let jobTypeFilter = $state("ALL");

  let companies = $state([]);
  let jobs = $state([]);
  let job = $state({
    title: null,
    description: null,
    earnings: null,
    jobType: null,
    companyId: null,
  });

  onMount(() => {
    if ($user?.user_roles?.includes("admin")) {
      getCompanies();
    }
    getJobs();
  });

  function changePage(pageNr) {
    currentPage = pageNr;
    getJobs();
  }

  function applyFilter() {
    currentPage = 1;
    getJobs();
  }

  function getCompanies() {
    var config = {
      method: "get",
      url: api_root + "/api/company",
      headers: {
        "Authorization": "Bearer " + $jwt_token
      },
    };

    axios(config)
      .then(function (response) {
        companies = response.data;
      })
      .catch(function (error) {
        alert("Could not get companies");
        console.log(error);
      });
  }

  function getJobs() {
    let query = `?pageSize=${defaultPageSize}&pageNumber=${currentPage}`;
    if (earningsMinFilter != null && earningsMinFilter !== '') {
        query += `&min=${earningsMinFilter}`;
    }
    if (jobTypeFilter && jobTypeFilter !== "ALL") {
        query += `&type=${jobTypeFilter}`;
    }

    var config = {
      method: "get",
      url: api_root + "/api/job" + query,
      headers: {
        "Authorization": "Bearer " + $jwt_token
      },
    };

    axios(config)
      .then(function (response) {
        jobs = response.data.content;
        nrOfPages = response.data.totalPages;
      })
      .catch(function (error) {
        alert("Could not get jobs");
        console.log(error);
      });
  }

  function createJob() {
    var config = {
      method: "post",
      url: api_root + "/api/job",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + $jwt_token
      },
      data: job,
    };

    axios(config)
      .then(function (response) {
        alert("Job created");
        changePage(1);
      })
      .catch(function (error) {
        alert("Could not create Job");
        console.log(error);
      });
  }

  function assignToMe(jobId) {
    var config = {
      method: "put",
      url: api_root + `/api/service/me/assignjob?jobId=${jobId}`,
      headers: {
        "Authorization": "Bearer " + $jwt_token
      },
    };

    axios(config)
      .then(function (response) {
        alert("Job assigned to you");
        getJobs(); // Refresh the job list
      })
      .catch(function (error) {
        alert("Could not assign job");
        console.log(error);
      });
  }

  function completeMyJob(jobId) {
    var config = {
      method: "put",
      url: api_root + `/api/service/me/completejob?jobId=${jobId}`,
      headers: {
        "Authorization": "Bearer " + $jwt_token
      },
    };

    axios(config)
      .then(function (response) {
        alert("Job marked as complete");
        getJobs(); // Refresh the job list
      })
      .catch(function (error) {
        alert("Could not complete job");
        console.log(error);
      });
  }
</script>

{#if $user?.user_roles?.includes("admin")}
  <h1 class="mt-3">Create Job</h1>
  <form class="mb-5">
    <div class="row mb-3">
      <div class="col">
        <label class="form-label" for="description">Title</label>
        <input
          bind:value={job.title}
          class="form-control"
          id="description"
          type="text"
        />
      </div>
    </div>
    <div class="row mb-3">
      <div class="col">
        <label class="form-label" for="description">Description</label>
        <input
          bind:value={job.description}
          class="form-control"
          id="description"
          type="text"
        />
      </div>
    </div>
    <div class="row mb-3">
      <div class="col">
        <label class="form-label" for="type">Type</label>
        <select bind:value={job.jobType} class="form-select" id="type">
          <option value="OTHER">OTHER</option>
          <option value="TEST">TEST</option>
          <option value="IMPLEMENT">IMPLEMENT</option>
          <option value="REVIEW">REVIEW</option>
        </select>
      </div>
      <div class="col">
        <label class="form-label" for="earnings">Earnings</label>
        <input
          bind:value={job.earnings}
          class="form-control"
          id="earnings"
          type="number"
        />
      </div>
      <div class="col">
        <label class="form-label" for="company">Company</label>
        <select bind:value={job.companyId} class="form-select" id="company">
          {#each companies as company}
            <option value={company.id}>{company.name}</option>
          {/each}
        </select>
      </div>
    </div>
    <button type="button" class="btn btn-primary" onclick={createJob}
      >Submit</button
    >
  </form>
{/if}

<h1>All Jobs</h1>

<div class="row my-3 align-items-center">
    <div class="col-auto">
        <label for="earningsFilter" class="col-form-label">Earnings:</label>
    </div>
    <div class="col-3">
        <input
            id="earningsFilter"
            type="number"
            class="form-control"
            placeholder="min"
            bind:value={earningsMinFilter} />
    </div>
    <div class="col-auto">
        <label for="jobTypeFilter" class="col-form-label">Job Type:</label>
    </div>
    <div class="col-3">
        <select class="form-select" id="jobTypeFilter" bind:value={jobTypeFilter}>
            <option value="ALL">ALL</option>
            <option value="OTHER">OTHER</option>
            <option value="TEST">TEST</option>
            <option value="IMPLEMENT">IMPLEMENT</option>
            <option value="REVIEW">REVIEW</option>
        </select>
    </div>
    <div class="col-auto">
        <button class="btn btn-primary" onclick={applyFilter}>Apply</button>
    </div>
</div>

<table class="table">
  <thead>
    <tr>
      <th scope="col">Title</th>
      <th scope="col">Description</th>
      <th scope="col">Type</th>
      <th scope="col">Earnings</th>
      <th scope="col">State</th>
      <th scope="col">CompanyId</th>
      <th scope="col">Actions</th>
    </tr>
  </thead>
  <tbody>
    {#each jobs as job (job.id)}
      <tr>
        <td><a href={"/jobdetails?id=" + job.id}>{job.title || 'N/A'}</a></td>
        <td>{job.description}</td>
        <td>{job.jobType}</td>
        <td>{job.earnings}</td>
        <td>{job.jobState}</td>
        <td>{job.companyId}</td>
        <td>
          {#if $isAuthenticated && job.jobState === 'AVAILABLE'}
            <button class="btn btn-success btn-sm" onclick={() => assignToMe(job.id)}>
              Assign to me
            </button>
          {/if}
          {#if $isAuthenticated && job.jobState === 'ASSIGNED' && job.freelancerId === $user?.email}
            <button class="btn btn-warning btn-sm ms-1" onclick={() => completeMyJob(job.id)}>
              Mark as Complete
            </button>
          {/if}
        </td>
      </tr>
    {/each}
  </tbody>
</table>

<nav aria-label="Job pagination">
    <ul class="pagination">
        {#each { length: nrOfPages } as _, i}
            <li class="page-item" class:active={currentPage === i + 1}>
                <button class="page-link" onclick={() => changePage(i + 1)}>
                    {i + 1}
                </button>
            </li>
        {/each}
    </ul>
</nav>

<style>
    .page-link:focus {
        box-shadow: none;
    }
</style>
